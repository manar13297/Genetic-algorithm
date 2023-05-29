package ma.enset;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class IslandAgent extends Agent {

    //Island attributes
    private Individual[] individuals =new Individual[GAUtils.POPULATION_SIZE];
    private Individual individual1;
    public Individual individual2;
    private int nbrIterations=0;
    //Island methods
    public void initialize(){
        for (int i=0;i<GAUtils.POPULATION_SIZE;i++) {
            individuals[i]=new Individual();
            individuals[i].calculateFintess();
        }
    }

    public void crossover(){
        individual1=new Individual(individuals[0].getChromosome());
        individual2=new Individual(individuals[1].getChromosome());

        Random random=new Random();
        int crossPoint=random.nextInt(GAUtils.CHROMOSOME_SIZE-1);
        crossPoint++;
        for (int i = 0; i <crossPoint ; i++) {
            individual1.getChromosome()[i]= individuals[1].getChromosome()[i];
            individual2.getChromosome()[i]= individuals[0].getChromosome()[i];
        }

    }
    public void showPopulation(){
        for (Individual individual: individuals) {
            System.out.println(Arrays.toString(individual.getChromosome())+" = "+individual.getFitness());
        }
    }
    public void sortPopulation(){
        Arrays.sort(individuals, Comparator.reverseOrder());
    }
    public void mutation(){
        Random random=new Random();
        if(random.nextDouble()>GAUtils.MUTATION_PROBABILITY){
            int AlphabeticIndex = random.nextInt(GAUtils.ALPHABETICS.length());
            int chromosomeIndex = random.nextInt(GAUtils.CHROMOSOME_SIZE);

            individual1.getChromosome()[chromosomeIndex]=GAUtils.ALPHABETICS.charAt(AlphabeticIndex);
        }
        if(random.nextDouble()>GAUtils.MUTATION_PROBABILITY){
            int AlphabeticIndex = random.nextInt(GAUtils.ALPHABETICS.length());
            int chromosomeIndex = random.nextInt(GAUtils.CHROMOSOME_SIZE);

            individual2.getChromosome()[chromosomeIndex]=GAUtils.ALPHABETICS.charAt(AlphabeticIndex);
        }
        individual1.calculateFintess();
        individual2.calculateFintess();
        individuals[GAUtils.POPULATION_SIZE-2]=individual1;
        individuals[GAUtils.POPULATION_SIZE-1]=individual2;


    }
    public int getBestFintness(){
        return individuals[0].getFitness();
    }
    public int getNbrIterations() {
        return nbrIterations;
    }

    public void setNbrIterations(int nbrIterations) {
        this.nbrIterations = nbrIterations;
    }




    @Override
    protected void setup() {
        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                initialize();
                showPopulation();
            }
        });

        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            @Override
            public void action() {
                crossover();
                mutation();
                sortPopulation();
                setNbrIterations(getNbrIterations()+1);
            }

            @Override
            public boolean done() {
                return GAUtils.MAX_ITERATIONS<=getNbrIterations() || getBestFintness() == GAUtils.CHROMOSOME_SIZE;
            }
        });
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("ga");
                dfAgentDescription.addServices(serviceDescription);
                DFAgentDescription[] defAgentDescriptions = null;
                try {
                    defAgentDescriptions= DFService.search(getAgent(), dfAgentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(defAgentDescriptions[0].getName());
                aclMessage.setContent(String.valueOf(getNbrIterations())+"/"+Arrays.toString(individuals[0].getChromosome())+"/"+String.valueOf(individuals[0].getFitness()));
                send(aclMessage);
            }
        });
        addBehaviour(sequentialBehaviour);
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
