package test

import org.scalatest._
import com.icegreen.greenmail.util.{ GreenMail, ServerSetup }

/** Specification for Testing an SMTP Server.
 *
 *  @note Should be mixed in last since it extends BeforeAndAfterAll
 */
trait MailSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
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

}