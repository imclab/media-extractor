package org.dbpedia.media_extractor.media_provider_session

import java.util.Properties
import java.util.Scanner

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.Api
import org.scribe.model.Response
import org.scribe.model.Token
import org.scribe.model.Verifier

// FIXME: find a more elegant way to pass the class as a parameter
abstract class MediaProviderOAuthSession[MyApi <: Api](
  val myApi: MyApi,
  val targetLicenses: String,
  val savedCredentialsFile: String,
  val savedAccessTokenFile: String) {

  val measurementUnit = "km"

  val endPointRootUri: String
  val maxResultsPerQuery: String
  val termsOfUseUri: String

  val savedAccessCredentialsProperties = loadPropertyFromFile(savedCredentialsFile)

  val myApiKey = savedAccessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = savedAccessCredentialsProperties.getProperty("apiKeySecret")

  val oAuthService = new ServiceBuilder()
    .provider(myApi)
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  val accessToken: Token =
    if ((!savedAccessTokenFile.isEmpty()) && (!(getSavedAccessToken(savedAccessTokenFile).isEmpty)))
      getSavedAccessToken(savedAccessTokenFile)
    else {
      val requestToken = oAuthService.getRequestToken()
      val authorizationUri = oAuthService.getAuthorizationUrl(requestToken)

      println("Follow this authorization URL to authorise yourself on " + myApi.getClass().toString() + ":")
      println(authorizationUri)
      println("Paste here the verifier it gives you:")
      print(">>")

      val scanner = new Scanner(System.in)
      val verifier = new Verifier(scanner.next())
      scanner.close()
      println("")

      val generatedAccessToken = oAuthService.getAccessToken(requestToken, verifier)

      println("Generated Access Token: (keep it secret!!)")
      println(generatedAccessToken)
      println("")

      generatedAccessToken
    }

  private def loadPropertyFromFile(propertyFile: String): Properties = {
    val propertyInputStream = this.getClass().getResourceAsStream(propertyFile)
    val myProperty = new Properties()

    myProperty.load(propertyInputStream)
    propertyInputStream.close()

    myProperty
  }

  def getSavedAccessToken(savedAccessTokenFile: String): Token = {
    val accessCredentialsProperties = loadPropertyFromFile(savedAccessTokenFile)

    val accessToken = accessCredentialsProperties.getProperty("accessToken")
    val accessSecret = accessCredentialsProperties.getProperty("accessSecret")

    new Token(accessToken, accessSecret)
  }

  def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response

  def getSearchResults(searchResponse: Response): List[SearchResult]

}

