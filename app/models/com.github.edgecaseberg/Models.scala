package com.github.edgecaseberg.models

import javax.mail.internet.InternetAddress

trait Emailable {
	def email: InternetAddress
}

/** Helper Companion to assist with Creating InternetAddress's
 *
 *  {{{
 *  import Emailable._
 *  User("username", "user@localhost")	// Will automatically convert
 *  User("username", "") // Will throw AddressException
 *  }}}
 */
object Emailable {
	implicit def string2Email(str: String): InternetAddress = new InternetAddress(str, true)
}

/** Dummy Class just to have an example */
case class User(val username: String, val email: InternetAddress) extends Emailable

/** Dummy Class just to have an example */
case class BillableClient(val businessName: String, val phone: String, val email: InternetAddress) extends Emailable
