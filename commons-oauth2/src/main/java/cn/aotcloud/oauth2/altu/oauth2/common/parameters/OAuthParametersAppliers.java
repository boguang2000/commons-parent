package cn.aotcloud.oauth2.altu.oauth2.common.parameters;

/**
 * @author xkxu
 */
public class OAuthParametersAppliers {

	private static final OAuthParametersApplier bodyEntityParametersApplier = new BodyEntityParametersApplier();
	
	private static final OAuthParametersApplier bodyURLEncodedParametersApplier = new BodyURLEncodedParametersApplier();
	
	private static final OAuthParametersApplier fragmentParametersApplier = new FragmentParametersApplier();
	
	private static final OAuthParametersApplier jsonBodyParametersApplier = new JSONBodyParametersApplier();
	
	private static final OAuthParametersApplier wwwAuthHeaderParametersApplier = new WWWAuthHeaderParametersApplier();
	
	private static final OAuthParametersApplier queryParameterApplier = new QueryParameterApplier();

	public static OAuthParametersApplier getBodyEntityParametersApplier() {
		return bodyEntityParametersApplier;
	}

	public static OAuthParametersApplier getBodyURLEncodedParametersApplier() {
		return bodyURLEncodedParametersApplier;
	}

	public static OAuthParametersApplier getFragmentParametersApplier() {
		return fragmentParametersApplier;
	}

	public static OAuthParametersApplier getJSONbodyParametersApplier() {
		return jsonBodyParametersApplier;
	}

	public static OAuthParametersApplier getWWWAuthheaderParametersApplier() {
		return wwwAuthHeaderParametersApplier;
	}

	public static OAuthParametersApplier getQueryParametersApplier() {
		return queryParameterApplier;
	}
	
}
