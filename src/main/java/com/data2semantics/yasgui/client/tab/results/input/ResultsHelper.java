package com.data2semantics.yasgui.client.tab.results.input;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import com.data2semantics.yasgui.client.helpers.ContentTypes;
import com.data2semantics.yasgui.client.helpers.ContentTypes.Type;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.Prefix;
import com.smartgwt.client.util.StringUtil;


/**
 * Interface for getting sparql or xml sparql result in a common object form
 */
public class ResultsHelper {
	public static String getLiteralFromBinding(HashMap<String, String> binding) {
		String literal = StringUtil.asHTML(binding.get("value"));
		if (binding.containsKey("xml:lang")) {
			literal = "\"" + literal + "\"@" + binding.get("xml:lang");
		} else if (binding.containsKey("datatype")) {
			String dataType = binding.get("datatype");
			literal = "\"" + literal;
			if (dataType.contains(ResultContainer.XSD_DATA_PREFIX)) {
				literal += "\"^^xsd:" + dataType.substring(ResultContainer.XSD_DATA_PREFIX.length());
			} else {
				literal += "\"^^<" + dataType + ">";
			}
		}
		return literal;
	}
	
	public static boolean tabularConstructContentType(String contentTypeString) {
		Type contentType = ContentTypes.detectContentType(contentTypeString);
		return tabularConstructContentType(contentType);
	}
	public static boolean tabularConstructContentType(Type contentType) {
		return (contentType == Type.CONSTRUCT_CSV || contentType == Type.CONSTRUCT_TSV);
	}
	/**
	 * Check for a uri whether there is a prefix defined in the query.
	 * 
	 * @param uri
	 * @return Short version of this uri if prefix is defined. Long version
	 *         otherwise
	 */
	public static String getShortUri(String uri, HashMap<String, Prefix> queryPrefixes) {
		for (Map.Entry<String, Prefix> entry : queryPrefixes.entrySet()) {
			String prefixUri = entry.getKey();
			if (uri.startsWith(prefixUri)) {
				uri = uri.substring(prefixUri.length());
				uri = entry.getValue().getPrefix() + ":" + uri;
				break;
			}
		}
		return uri;
	}

	
	
	
	
	public static String getSnorqlHrefLink(HashMap<String, String> binding, HashMap<String, Prefix> queryPrefixes) {
		String uri = binding.get("value");
		return "<span class=\"clickable\" onclick=\"queryForResource('" + uri + "');\">" + ResultsHelper.getShortUri(uri, queryPrefixes) + "</span>";
	}


	public static String getRegularHrefLink(HashMap<String, String> binding, HashMap<String, Prefix> queryPrefixes) {
		String uri = binding.get("value");
		return  "<a href=\"" + uri + "\" target=\"_blank\">" + StringUtil.asHTML(getShortUri(uri, queryPrefixes)) + "</a>";
	}

	public static boolean valueIsUri(HashMap<String, String> valueInfo) {
		if (valueInfo.containsKey("type")) {
			return valueInfo.get("type").equals("uri");
		} else {
			//try to detect manually. a csv returned from an endpoint does not contain this extra information
			return Helper.validUrl(valueInfo.get("value"));
		}
	}
	public static boolean valueIsLiteral(HashMap<String, String> valueInfo) {
		return (valueInfo.containsKey("type") && (valueInfo.get("type").equals("literal") || valueInfo.get("type").equals("typed-literal")));
	}
}


