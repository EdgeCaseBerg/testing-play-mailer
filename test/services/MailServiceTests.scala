package services

import org.scalatest._

import play.api.test.Helpers._
import play.api.test._

import com.github.edgecaseberg.services._
import com.github.edgecaseberg.models._

import scala.concurrent.ExecutionContext.Implicits.global

class MailServiceTests extends test.MailSpec {
	import Emailable._

	val mailService = new MailService()
	val myUser = User("username", "user@localhost")
	val client = BillableClient("important client", "555-555-5555", "business@localhost")

	def fakeApp = {
		FakeApplication(
			additionalConfiguration = Map(
				"smtp.host" -> "localhost",
				"smtp.port" -> mailPort,
				"smtp.ssl" -> false
			)
		)
	}

	"The MailService" should "send mail!" in running(fakeApp) {
		mailService.sendMail("admin@localhost", "user@localhost", "A Test", "plain", "<html><body><h1>Hi!</h1></body></html>")
		waitForInbox(2000)
		val emails = getMessageForEmail("user@localhost")
		assertResult(1)(emails.size)
		assertResult(List("admin@localhost"))(emails.head.from)
		assertResult(List("user@localhost"))(emails.head.to)
		assertResult("A Test")(emails.head.subject)
		assert(emails.head.plain.isDefined)
		assertResult("plain")(emails.head.plain.get)
		assert(emails.head.html.isDefined)
		assertResult("<html><body><h1>Hi!</h1></body></html>")(emails.head.html.get)
	}

	it should "send welcome mail to a User" in running(fakeApp) {
		val user = User("name", "user@localhost")
		mailService.sendWelcomeEmailTo(user)
		waitForInbox(2000)
		val emails = getMessageForEmail(user.email.getAddress())
		assertResult(1)(emails.size)
		assertResult(List(mailService.fromAddress))(emails.head.from)
		assertResult(List("user@localhost"))(emails.head.to)
		assertResult(mailService.welcomeSubject)(emails.head.subject)
		assert(emails.head.plain.isDefined)
		assert(emails.head.html.isDefined)
		val plainText: String = views.txt.email.welcome(user.username, mailService.fromAddress).body
		val htmlText: String = views.html.email.welcome(user.username, mailService.fromAddress).body
		assertResult(plainText, "Plain text was incorrect")(emails.head.plain.get)
		assertResult(htmlText, "HTML text was incorrect")(emails.head.html.get)
	}
}