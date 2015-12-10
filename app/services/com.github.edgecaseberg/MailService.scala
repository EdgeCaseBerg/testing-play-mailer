package com.github.edgecaseberg.services

import com.github.edgecaseberg.models._
import com.typesafe.plugin.{ MailerAPI, MailerPlugin, _ }

import play.api.Play.current
import play.api.libs.concurrent.Akka

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MailService(implicit ec: ExecutionContext) {
	val fromAddress = "tester@domain.com" //IRL pull from configuration
	val welcomeSubject = "Welcome!"

	def sendWelcomeEmailTo(to: Emailable) {

		val (name, emailable) = to match {
			case User(username, email) => (username, email)
			case BillableClient(name, _, email) => (name, email)
			case _ => ("", to.email)
		}

		val sendTo = emailable.getAddress()
		val plainText: String = views.txt.email.welcome(name, fromAddress).body
		val htmlText: String = views.html.email.welcome(name, fromAddress).body
		sendMail(fromAddress, sendTo, welcomeSubject, plainText, htmlText)
	}

	def sendMail(from: String, to: String, subject: String, plainTextBody: String, htmlBody: String): Unit = Akka.system.scheduler.scheduleOnce(1.second) {
		val email: MailerAPI = use[MailerPlugin].email

		email.setRecipient(to)
		email.setFrom(from)
		email.setSubject(subject)

		email.send(plainTextBody, htmlBody)
	}
}
