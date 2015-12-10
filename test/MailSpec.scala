package test

import org.scalatest._
import com.icegreen.greenmail.util.{ GreenMail, ServerSetup }
import scala.collection.JavaConverters._

import javax.mail.internet.MimeMessage
import javax.mail._

case class SimpleEmail(to: List[String], from: List[String], subject: String, plain: Option[String], html: Option[String])

/** Specification for Testing an SMTP Server.
 *
 *  @note Should be mixed in last since it extends BeforeAndAfterAll
 */
trait MailSpec extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll {
	/** The current mailPort for this test
	 *
	 *  @note A def so that it can be overridden by users of the test
	 */
	def mailPort = {
		9001
	}

	val greenMail = new GreenMail(new ServerSetup(mailPort, null, "smtp"));

	override def beforeAll() {
		super.beforeAll()
		greenMail.start()
	}

	override def afterAll() {
		super.afterAll()
		greenMail.stop();
	}

	override def beforeEach() {
		clearMailboxes
		super.beforeEach()
	}

	def clearMailboxes = {
		val managers = greenMail.getManagers()
		val users = managers.getUserManager().listUser().asScala
		val imapManager = managers.getImapHostManager()
		users.map(imapManager.getInbox(_)).map(_.deleteAllMessages())
	}

	def waitForInbox(milliseconds: Long) {
		greenMail.waitForIncomingEmail(milliseconds, 1)
	}

	def getMessageForEmail(email: String): List[SimpleEmail] = {
		val user = greenMail.setUser(email, null)
		val inbox = greenMail.getManagers().getImapHostManager().getInbox(user)
		inbox.getMessages().asScala.sortWith(
			(l, r) => l.getReceivedDate().after(r.getReceivedDate())
		).map(msg => mimeMessageToSimpleEmail(msg.getMimeMessage())).toList
	}

	def getTextFromBodyPart(p: javax.mail.Part): Option[String] = {
		if (p.isMimeType("text/*")) {
			Some(p.getContent().asInstanceOf[String])
		} else {
			None
		}
	}

	def arr2List[T](arr: Array[T]): List[T] = {
		arr.toList
	}

	def mimeMessageToSimpleEmail(mimeMsg: MimeMessage): SimpleEmail = {
		val subject = mimeMsg.getSubject()
		val senders = arr2List(mimeMsg.getReplyTo()).map(_.toString())
		val recipients = arr2List(mimeMsg.getAllRecipients()).map(_.toString())
		/* Now the annoying part. The Message itself and all the disposition and such */
		val mailParts = mimeMsg.getContent().asInstanceOf[Multipart]
		var plainText = Option[String](null)
		var htmlText = Option[String](null)
		for (p <- 0 until mailParts.getCount()) {
			/* Code adapted from studying javadocs and http://www.coderanch.com/t/597373/java/java/Body-text-javamail-retrieve-email */
			val bodyPart = mailParts.getBodyPart(p)
			if (bodyPart.isMimeType("text/*")) {
				if (bodyPart.isMimeType("text/html")) {
					htmlText = getTextFromBodyPart(bodyPart)
				} else {
					plainText = getTextFromBodyPart(bodyPart)
				}
			} else if (bodyPart.isMimeType("multipart/*")) {
				val multiPart = bodyPart.getContent().asInstanceOf[Multipart]
				for (mp <- 0 until multiPart.getCount()) {
					val part = multiPart.getBodyPart(mp)
					if (part.isMimeType("text/plain")) {
						plainText = getTextFromBodyPart(part)
					} else if (part.isMimeType("text/html")) {
						htmlText = getTextFromBodyPart(part)
					}
				}
			}
		}

		SimpleEmail(recipients, senders, subject, plainText, htmlText)
	}

}