package services

import org.scalatest._

import play.api.test.Helpers._
import play.api.test._

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
		greenMail.start()
	}

	override def afterAll() {
		greenMail.stop();
	}

}

class MailServiceTests extends MailSpec {
	"The MailService" should "send mail!" in {
		fail("Not Implemented Yet :(")
	}
}