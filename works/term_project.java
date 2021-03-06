import java.util.Scanner;

import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

public class awsTest{
        /*
         * Cloud Computing, Data Computing Laboratory
         * Department of Computer Science
         * Chungbuk National University
         */
        static AmazonEC2      ec2;
        private static void init() throws Exception {
                /*
                 * The ProfileCredentialsProvider will return your [default]
                 * credential profile by reading from the credentials file located at
                 * (~/.aws/credentials).
                 */
                ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
                try {
                        credentialsProvider.getCredentials();
                } catch (Exception e) {
                        throw new AmazonClientException(
                                "Cannot load the credentials from the credential profiles file. " +
                                "Please make sure that your credentials file is at the correct " +
                                "location (~/.aws/credentials), and is in valid format.",
                                e);
                }
                ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-2") /* check the region at AWS console */
                .build();
        }
        public static void main(String[] args) throws Exception {

                init();

                Scanner menu = new Scanner(System.in);
                Scanner id_string = new Scanner(System.in);
                int number = 0;
		String instance_id;

		while(true)

                {
                        System.out.println("                                                            ");
                        System.out.println("                                                            ");
                        System.out.println("------------------------------------------------------------");
                        System.out.println("           Amazon AWS Control Panel using SDK               ");
                        System.out.println("                                                            ");
                        System.out.println("  Cloud Computing, Computer Science Department              ");
                        System.out.println("                           at Chungbuk National University  ");
                        System.out.println("------------------------------------------------------------");
                        System.out.println("  1. list instance                2. available zones         ");
                        System.out.println("  3. start instance               4. available regions      ");
                        System.out.println("  5. stop instance                6. create instance        ");
                        System.out.println("  7. reboot instance              8. list images            ");
                        System.out.println("                                 99. quit                   ");
                        System.out.println("------------------------------------------------------------");
                        System.out.print("Enter an integer: ");

                        number = menu.nextInt();
                        switch(number) {
                        case 1:
                                        listInstances();
                                        break;
                        case 2:
                                        availablezones();
                                        break;
			case 3: 
					instance_id = id_string.nextLine();
					startInstance(instance_id);
					break;
			case 4:
					availableregion();
					break;
			case 5: 
					instance_id = id_string.nextLine(); 
					stopInstance(instance_id);
					break;
                        }
                }
        }

	public static void listInstances()
        {
                ec2 = AmazonEC2ClientBuilder.defaultClient();
                System.out.println("Listing instances....");
                boolean done = false;
                DescribeInstancesRequest request = new DescribeInstancesRequest();
                while(!done) {
                        DescribeInstancesResult response = ec2.describeInstances(request);
                        for(Reservation reservation : response.getReservations()) {
                                for(Instance instance : reservation.getInstances()) {
                                        System.out.printf(
                                                "[id] %s, " +
                                                "[AMI] %s, " +
                                                "[type] %s, " +
                                                "[state] %10s, " +
                                                "[monitoring state] %s",
                                                instance.getInstanceId(),
                                                instance.getImageId(),
                                                instance.getInstanceType(),
                                                instance.getState().getName(),
                                                instance.getMonitoring().getState());
                                }
                                System.out.println();
                        }
                        request.setNextToken(response.getNextToken());
                        if(response.getNextToken() == null) {
                                done = true;
                        }
                }
        }
        public static void availablezones(){
                ec2 = AmazonEC2ClientBuilder.defaultClient();
                DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

                for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
                        System.out.printf(
                        "[id] %s, " +
                        "[region] %s, " +
                        "[zone] %s",
                        zone.getZoneId(),
                        zone.getRegionName(),
                        zone.getZoneName());
                        System.out.println();
                }

        }
	
	public static void availableregion(){
		ec2 = AmazonEC2ClientBuilder.defaultClient();
		DescribeRegionsResult regions_response = ec2.describeRegions();

        	for(Region region : regions_response.getRegions()) {
            	System.out.printf(
                	"[region] %s " +
                	"[endpoint] %s",
                	region.getRegionName(),
                	region.getEndpoint());
			System.out.println();
        	}
	}

	public static void stopInstance(String instance_id)
    	{
        	ec2 = AmazonEC2ClientBuilder.defaultClient();

        	DryRunSupportedRequest<StopInstancesRequest> dry_request = 
			() -> { 
			StopInstancesRequest request = new StopInstancesRequest()
                		.withInstanceIds(instance_id);

            		return request.getDryRunRequest();
        	};

        	DryRunResult dry_response = ec2.dryRun(dry_request);

        	if(!dry_response.isSuccessful()) {
            		System.out.printf(
                		"Failed dry run to stop instance %s", instance_id);
            		throw dry_response.getDryRunResponse();
        	}

        	StopInstancesRequest request = new StopInstancesRequest()
            		.withInstanceIds(instance_id);

        	ec2.stopInstances(request);

        	System.out.printf("Successfully stop instance %s", instance_id);
    	}

	public static void startInstance(String instance_id)
    	{
        	ec2 = AmazonEC2ClientBuilder.defaultClient();

        	DryRunSupportedRequest<StartInstancesRequest> dry_request =
            	() -> {
            	StartInstancesRequest request = new StartInstancesRequest()
                	.withInstanceIds(instance_id);

            		return request.getDryRunRequest();
        	};

        	DryRunResult dry_response = ec2.dryRun(dry_request);

        	if(!dry_response.isSuccessful()) {
            	System.out.printf(
                	"Failed dry run to start instance %s", instance_id);

            	throw dry_response.getDryRunResponse();
        	}

        	StartInstancesRequest request = new StartInstancesRequest()
            	.withInstanceIds(instance_id);

        	ec2.startInstances(request);

        	System.out.printf("Successfully started instance %s", instance_id);
    	}
}

