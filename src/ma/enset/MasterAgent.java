package ma.enset;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class MasterAgent extends Agent {


@Override
    protected void setup() {
        ACLMessage aclMessage = null;
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();

        //Définir le service à etre publié ;
        serviceDescription.setName("master");
        serviceDescription.setType("ga");
        //Agouter le service à notre df
        dfAgentDescription.addServices(serviceDescription);
        try {
            //Publier le service
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage receive = receive();
                if(receive!=null ){
                    System.out.println("From: "+receive.getSender().getName()+" Got "+(receive.getContent().split("/"))[1]+" With fitness value="+receive.getContent().split("/")[2]);
                    if(receive.getContent().split("/")[2].equals(String.valueOf(GAUtils.CHROMOSOME_SIZE))){
                        System.out.println("|----------------------------------------------------------------------------|");
                        System.out.println("|GOT BEST SOLUTION FROM "+receive.getSender().getLocalName()+ " ----> "+receive.getContent().split("/")[1]+"|");
                        System.out.println("|____________________________________________________________________________|");
                    System.out.println("\n");}
                    if(receive.getContent().split("/")[2].equals(String.valueOf(GAUtils.CHROMOSOME_SIZE-1))){
                        System.out.println("|----------------------------------------------------------------------------|");
                        System.out.println("|APPROXIMATIVE SOLUTION FROM "+receive.getSender().getLocalName()+ " ----> "+receive.getContent().split("/")[1]+"|");
                        System.out.println("|____________________________________________________________________________|");
                        System.out.println("\n");}
                }else {
                    block();

                }

            }
        });
    }



    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
