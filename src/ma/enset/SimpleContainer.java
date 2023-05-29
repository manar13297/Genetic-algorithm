package ma.enset;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class SimpleContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);

        AgentController master=agentContainer.createNewAgent("master", MasterAgent.class.getName(),new Object[]{});
        master.start();

        for (int i = 0; i < GAUtils.ISLAND_NUMBER; i++) {
            int j=i+1;
            AgentController island=agentContainer.createNewAgent("island"+j, IslandAgent.class.getName(),new Object[]{});
            island.start();
        }
    }
}