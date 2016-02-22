package cmanager;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.Token;

public class OKAPI_OAUTH extends DefaultApi10a
{
  @Override
  public String getAccessTokenEndpoint()
  {
    return "https://www.opencaching.de/okapi/services/oauth/access_token";
  }

  @Override
  public String getRequestTokenEndpoint()
  {
    return "https://www.opencaching.de/okapi/services/oauth/request_token";
  }

  @Override
  public String getAuthorizationUrl(Token requestToken)
  {
    return String.format("https://www.opencaching.de/okapi/services/oauth/authorize?oauth_token=%s",
    		requestToken.getToken());
  }
}