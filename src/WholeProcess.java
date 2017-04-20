import java.io.IOException;
import java.sql.SQLException;

public class WholeProcess {
	


	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		ConnSelectedData csd = new ConnSelectedData();
		IndexBuilder ib = new IndexBuilder();
		FeatureExtration fe = new FeatureExtration();
		
//		csd.getSelectedData();
		ib.main(null);
		System.out.println("finish ifa");
		fe.featureExtration();
		
	}

}
