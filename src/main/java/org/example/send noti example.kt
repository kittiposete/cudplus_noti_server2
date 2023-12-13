package org.example


fun main() {
    val deviceToken =
        "cqCLI6MoQByZSm8aQfseVX:APA91bFuDBlMuFit4QTZ4YGj1kyPpULdTlqInP6DbCaxrOgqVlSW9VVDhKDPg46UQMriRdAI0sBEwfPRfjC32zNgVLhVwCg009mNFsQUOY3gl83PzK-wDa3Z3KOgajFao6PYwAnMObC6"
    val body = "test body"
    PushNotification().pushNotification("cudPower", body, deviceToken)
}