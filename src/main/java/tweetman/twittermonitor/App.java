package tweetman.twittermonitor;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class App 
{
	public static void run(String consumerKey, String consumerSecret, String token, String secret, String... terms) throws InterruptedException {
	    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
	    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
	    // add some track terms
	    endpoint.trackTerms(Lists.newArrayList(terms));
	    
	    Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
	    // Authentication auth = new BasicAuth(username, password);

	    // Create a new BasicClient. By default gzip is enabled.
	    Client client = new ClientBuilder()
	            .hosts(Constants.STREAM_HOST)
	            .endpoint(endpoint)
	            .authentication(auth)
	            .processor(new StringDelimitedProcessor(queue))
	            .build();

	    // Establish a connection
	    client.connect();
	    try {
		    // Do whatever needs to be done with messages
		    while (true) {
		      String msg = queue.take();
		      
		      System.out.println(msg);
		    }
	    } finally {
	    	client.stop();
	    }
	}
	
    public static void main( String[] args )
    {
        if (args.length < 5) {
        	System.err.println("invalid arguments <consumerKey> <consumerSecret> <token> <secret> <terms...>");
        	for (int i = 0; i < args.length; ++i) {
        		System.err.println(args[i]);
        	}
        	System.exit(1);
        }
    	
        try
        {
        	run(args[0], args[1], args[2], args[3], Arrays.copyOfRange(args, 4, args.length));
        }
        catch (InterruptedException e)
        {
        	System.err.println(e.getMessage());
        }
        System.out.println("done");
    }
}
