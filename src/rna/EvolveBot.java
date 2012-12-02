package rna;

import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



/**
 * All the different evolvebots are classes that inherit everything from this one; they are only differentiated from one another by loading different geneticcode.rna files
*/
public class EvolveBot extends AdvancedRobot
{
	public static int numberOfGenes=9;//TODO: rewrite as method
	private static int[] eventValueLengths={0,0,5,4,2,1,0,0,0};
 	public static int[] junkThreasholds(){
 		int[] threasholds=new int[numberOfGenes];
 		for(int i=0;i<numberOfGenes;i++){
 			threasholds[i]=25+5*eventValueLengths[i];
 		}
 		return threasholds;
 	}
	private String[] genes=new String[numberOfGenes];
	
	
	/*
	 * eventValues.length must be <25 otherwise the latter values will never get used
	 */
	void ActOnGene(String gene,double[] eventValues){
		double workingValue=0;//reset this each time
		boolean skipping=false;
		for(int i=0;i< gene.length();i+=4){
			int command=Integer.parseInt(gene.substring(i,i+2), 16);
			int value=Integer.parseInt(gene.substring(i+2,i+4), 16);
			if(!skipping){
				switch(command){
				case 0:
					//zero is reserved as a loop terminator
					skipping=false;
					break;
				case 1:
					ahead(workingValue);
					break;
				case 2:
					back(workingValue);
					break;
				case 3:
					fire(workingValue/80);//TODO scale?
					break;
				case 4:
					turnGunLeft(workingValue);
					break;
				case 5:
					turnGunRight(workingValue);
					break;
				case 6:
					turnRadarLeft(workingValue);
					break;
				case 7:
					turnRadarRight(workingValue);
					break;
				case 8:
					turnLeft(workingValue);
					break;
				case 9:
					turnRight(workingValue);
					break;
				case 10:
					execute();
					break;
				case 11:
					setAhead(workingValue);
					break;
				case 12:
					setBack(workingValue);
					break;
				case 13:
					setFire(workingValue/80);//TODO scale?
					break;
				case 14:
					setMaxTurnRate(workingValue/25);
					break;
				case 15:
					setMaxVelocity(workingValue/32);
					break;
				case 16:
					setTurnGunLeft(workingValue);
					break;
				case 17:
					setTurnGunRight(workingValue);
					break;
				case 18:
					setTurnLeft(workingValue);
					break;
				case 19:
					setTurnRight(workingValue);
					break;
				case 20:
					setTurnRadarLeft(workingValue);
					break;
				case 21:
					setTurnRadarRight(workingValue);
					break;
				case 22:
					workingValue+=value;
					break;
				case 23:
					workingValue-=value;
					break;
				case 24:
					workingValue*=value;
					break;
				case 25:
					workingValue/=value;
					break;
				case 26:
					workingValue=value;
					break;
				case 27:
					if(workingValue>value)
						skipping=true;
					break;
				case 28:
					if(workingValue<value)
						skipping=true;
					break;
				case 29:
					if(workingValue==value)
						skipping=true;
					break;
				default:
					int staticCommands=30;
					if(command<staticCommands+eventValues.length){
						workingValue=eventValues[command-staticCommands]; 
					}else if(command<staticCommands+2*eventValues.length){
						workingValue+=eventValues[command-(staticCommands+eventValues.length)]; 
					}else if(command<staticCommands+3*eventValues.length){
						workingValue-=eventValues[command-(staticCommands+2*eventValues.length)]; 
					}else if(command<staticCommands+4*eventValues.length){
						workingValue*=eventValues[command-(staticCommands+3*eventValues.length)]; 
					}else if(command<staticCommands+5*eventValues.length){
						workingValue/=eventValues[command-(staticCommands+4*eventValues.length)]; 
					}
					break;//otherwise, do nothing- junk rna
				}
			}
		}
	}
	
	
	public void run() {

	//Load the genome for this bot and split it into sections for the different possible events
	FileReader rnaFile = null;
		try {
			rnaFile = new FileReader(getDataFile("geneticcode.rna"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(rnaFile);
		try {
			//This is where we use the geneticcode data to set the behaviour
			for(int i=0;i<numberOfGenes;i++){
				genes[i]=br.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] eventValues={};
		ActOnGene(genes[0],eventValues);
		while (true) {
			ActOnGene(genes[1],eventValues);
		}
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		double[] eventValues={e.getBearing(),e.getDistance(),e.getEnergy(),e.getHeading(),e.getVelocity()};
		ActOnGene(genes[2],eventValues);
	}
	public void onHitByBullet(HitByBulletEvent e){
		double[] eventValues={e.getBearing(),e.getHeading(),e.getPower(),e.getVelocity()};
		ActOnGene(genes[3],eventValues);
	}
	public void onHitRobot(HitRobotEvent e){
		double[] eventValues={e.getBearing(),e.getEnergy()};
		ActOnGene(genes[4],eventValues);
	}
	public void onHitWall(HitWallEvent e){
		double[] eventValues={e.getBearing()};
		ActOnGene(genes[5],eventValues);
	}
	public void onBulletHitBullet(BulletHitBulletEvent e){
		double[] eventValues={};
		ActOnGene(genes[6],eventValues);
	}
	public void onBulletHit(BulletHitEvent e){
		double[] eventValues={e.getEnergy()};
		ActOnGene(genes[7],eventValues);
	}
	public void onBulletMissed(BulletMissedEvent e){
		double[] eventValues={};
		ActOnGene(genes[8],eventValues);
	}
}
