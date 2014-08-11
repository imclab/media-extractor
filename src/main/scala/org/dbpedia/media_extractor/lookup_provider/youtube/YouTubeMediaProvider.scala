package org.dbpedia.media_extractor.lookup_provider.youtube

import org.dbpedia.media_extractor.lookup_provider.MediaProvider
import org.dbpedia.media_extractor.lookup_provider.MediaProviderOAuthSession
import org.scribe.builder.api.GoogleApi

abstract class YouTubeMediaProvider(
  override val targetLicenses: String,
  override val mediaProviderOAuthSession: MediaProviderOAuthSession[GoogleApi])

  extends MediaProvider(mediaProviderOAuthSession, targetLicenses) {
  // TODO: complete this empty stub
}
