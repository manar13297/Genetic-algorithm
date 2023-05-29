package ma.enset;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MainContainer {
    public static void main(String[] args) {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter("gui","true");
        AgentContainer mainContainer=runtime.createMainContainer(profile);
        try {
            mainContainer.start();
            System.out.println("Main container started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start main container: " + e);
        }

    }
}
