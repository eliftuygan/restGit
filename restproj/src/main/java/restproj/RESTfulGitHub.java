package restproj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/")
public class RESTfulGitHub {
	
	/*
	 * 
	 * check each page of contributers till it returns null
	 * add each commit to json array 
	 * add contributions(total commit) as a key and login(username) as a value to map
	 * sort map values
	 * get top 5 value and its key
	 * returns Http response
	 *  
	*/

	@GET
	@Produces("text/html")
	public Response getStartingPage() throws Exception {
		
		int totalCommitCount = 0;
		int totalContributorsCount = 0;
		int pageNum = 1;
		String topFiveContributors= "";
		String index;
		Map<Integer, String> map = new TreeMap<Integer, String>(Collections.reverseOrder());

		while (true) {  
			
			HttpURLConnection httpcon = (HttpURLConnection) new URL(String.format(
					"https://api.github.com/repos/apache/commons-lang/contributors?page=%d&per_page=%d", pageNum, 100))
							.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
			index = in.readLine(); 

			if (!index.equals(null) && !index.equalsIgnoreCase("[]")) {

				JSONArray jasonArr = new JSONArray(index); //keep all key and values in jasonArr for per page 

				for (int i = 0; i < jasonArr.length(); i++) {
					JSONObject json = jasonArr.getJSONObject(i);

					if (!json.get("login").equals(null)) { 
						map.put(Integer.parseInt(json.get("contributions").toString()),json.get("login").toString()); //map<NumberofCommmits,Username>
						totalCommitCount += Integer.parseInt(json.get("contributions").toString()); 
					}
				}
				totalContributorsCount += jasonArr.length();
				pageNum++;

			} else {
				in.close();
				break;
				
			}
			
		}
		
		//get the top 5 contributors in all 
		int i = 0;
			 for (Map.Entry<Integer, String> entry : map.entrySet()) {
				  if(i<5) {
					  System.out.println(entry.getKey() + "/" + entry.getValue());
					  topFiveContributors += "  Username :   " + entry.getValue() + " -->  NumofCommits  : " + entry.getKey() + "<br>";
					  i++;
				  }else {
					  break;
				  }
			}
		 
		
		String output = "<h1>RESTful Service is running ... <br>Ping @ " + new Date().toString()
				+ "</h1><br>" + "https://api.github.com/repos/apache/commons-lang" + "<br>"+  "  TOTAL COUNT of COMMITS:  " + totalCommitCount + "<br> TOTAL CONTRIBUTORS  : " + totalContributorsCount + "<br>"  + "TOP 5 CONTRIBUTORS  =  <br>" + topFiveContributors;

		return Response.status(200).entity(output).build();

	}

}

//403 ERROR --- AUTHENTICATE --- REQUEST LIMIT FOR API.GITHUB IS: 60 
/*		https://api.github.com/repos/apache/commons-lang/commits

//NOTE : ---- WHEN I COUNT COMMITS -  IF IT HAS MORE THAN 60 PAGE ---  IT GIVES 403 ERROR

{
	  "message": "API rate limit exceeded for 24.133.172.124. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
	  "documentation_url": "https://developer.github.com/v3/#rate-limiting"
	}			
*/
