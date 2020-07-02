package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import simulation.Rescuable;
import simulation.Simulator;
import view.MyWindow;

public class CommandCenter implements SOSListener, ActionListener {

	private Simulator engine;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private MyWindow window;
	private boolean uclick = false;
	private boolean bcclick = false;
	private int x;
	private int y;
	
	private ArrayList <Citizen> arrived ;

	@SuppressWarnings("unused")
	private ArrayList<Unit> emergencyUnits;

	public CommandCenter() throws Exception {
		engine = new Simulator(this);

		visibleBuildings = new ArrayList<ResidentialBuilding>();
		visibleCitizens = new ArrayList<Citizen>();
		emergencyUnits = engine.getEmergencyUnits();
		window = new MyWindow(this);
		engine.setWindow(window);
		window.setVisible(true);
		arrived=new ArrayList<Citizen>();
	}
	
	public String checkcitizen() {
		String m="";
		for(int i =0;i<arrived.size();i++) {
			Citizen c=(Citizen)arrived.get(i);
			m = m + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
					+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge() + "\nHP: "
					+ c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: " + c.getToxicity()
					+ "\nState: " + c.getState();
		}
		
		return  m;
		
	}
	

	public ArrayList<Citizen> getArrived() {
		return arrived;
	}

	public void setArrived(ArrayList<Citizen> arrived) {
		this.arrived = arrived;
	}

	public void addtogrid() {
		for (int i = 0; i < visibleBuildings.size(); i++) {

			ResidentialBuilding b = visibleBuildings.get(i);
			JButton x = window.getArr()[b.getLocation().getX()][b.getLocation().getY()];
			if (b.getStructuralIntegrity() == 0) {
				ImageIcon img5 = new ImageIcon("buildingcollapse.png");
				Image z5 = img5.getImage();
				Image newimg5 = z5.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
				img5 = new ImageIcon(newimg5);
				x.setIcon(img5);
			} else {
				ImageIcon img5 = new ImageIcon("building.png");
				Image z5 = img5.getImage();
				Image newimg5 = z5.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
				img5 = new ImageIcon(newimg5);
				x.setIcon(img5);
			}
		}
		for (int i = 0; i < visibleCitizens.size(); i++) {
			Citizen c = visibleCitizens.get(i);
			JButton x = window.getArr()[c.getLocation().getX()][c.getLocation().getY()];
			if (c.getState() == CitizenState.DECEASED) {
				ImageIcon img6 = new ImageIcon("deadman.jpg");
				Image z6 = img6.getImage();
				Image newimg6 = z6.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
				img6 = new ImageIcon(newimg6);
				x.setIcon(img6);

			} else {
				ImageIcon img6 = new ImageIcon("aliveman.png");
				Image z6 = img6.getImage();
				Image newimg6 = z6.getScaledInstance(100, 50, Image.SCALE_SMOOTH);
				img6 = new ImageIcon(newimg6);
				x.setIcon(img6);
			}
		}
	}

	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!visibleBuildings.contains(r))
				visibleBuildings.add((ResidentialBuilding) r);

		} else {

			if (!visibleCitizens.contains(r))
				visibleCitizens.add((Citizen) r);
		}

	}

	public Rescuable gettheobject(int a, int b) {
		for (int i = 0; i < visibleBuildings.size(); i++) {
			if (visibleBuildings.get(i).getLocation().getX() == a
					&& visibleBuildings.get(i).getLocation().getY() == b) {
				ResidentialBuilding r = (ResidentialBuilding) visibleBuildings.get(i);
				
				return r;

			}

		}
		for (int k = 0; k < visibleCitizens.size(); k++) {
			if (visibleCitizens.get(k).getLocation().getX() == a && visibleCitizens.get(k).getLocation().getY() == b) {
				Citizen c = (Citizen) visibleCitizens.get(k);
				
				return c;
			}
		}

		return null;

	}

	public String find(int x, int y) {
		String s = "";
		for (int i = 0; i < visibleBuildings.size(); i++) {
			if (visibleBuildings.get(i).getLocation().getX() == x
					&& visibleBuildings.get(i).getLocation().getY() == y) {
				ResidentialBuilding r = (ResidentialBuilding) visibleBuildings.get(i);
				String d = "";
				if (r.getDisaster() instanceof Collapse) {
					d = "Collapse";
				} else if (r.getDisaster() instanceof Fire) {
					d = "Fire";
				} else if (r.getDisaster() instanceof GasLeak) {
					d = "Gas Leak";
				} else if (r.getDisaster() instanceof Infection) {
					d = "Infection";
				} else if (r.getDisaster() instanceof Injury) {
					d = "Injury";
				}
				s = s + "Location: " + r.getLocation().getX() + "," + r.getLocation().getY() + "\nStructural Integrity:"
						+ r.getStructuralIntegrity() + "\nFoundation Damage: " + r.getFoundationDamage()
						+ "\nFire Damage: " + r.getFireDamage() + "\nGas Level: " + r.getGasLevel()
						+ "\nNo. of occupants: " + r.getOccupants().size() + "\nDisaster's Type: " + d
						+ "\nOccupants:\n";
				for (int j = 0; j < r.getOccupants().size(); j++) {
					Citizen c = (Citizen) r.getOccupants().get(j);
					s = s + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
							+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge() + "\nHP: "
							+ c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: " + c.getToxicity()
							+ "\nState: " + c.getState();
				}

			}

		}
		for (int k = 0; k < visibleCitizens.size(); k++) {
			if (visibleCitizens.get(k).getLocation().getX() == x && visibleCitizens.get(k).getLocation().getY() == y) {
				for (int j = 0; j < visibleCitizens.size(); j++) {
					Citizen c = (Citizen) visibleCitizens.get(j);
					String d = "";
					if (c.getDisaster() instanceof Infection) {
						d = "Infection";
					} else if (c.getDisaster() instanceof Injury) {
						d = "Injury";
					}
					s = s + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
							+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge() + "\nHP: "
							+ c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: " + c.getToxicity()
							+ "\nState: " + c.getState() + "\nDisaster's Type: " + d;
				}

			}
		}
		return s;
	}

	public static void main(String[] args) throws Exception {
		new CommandCenter();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton a = (JButton) e.getSource();
		
		
		for (int i = 0; i < window.getArr().length; i++) {
			for (int j = 0; j < window.getArr()[i].length; j++) {
				if (a == window.getArr()[i][j]) {
					for(int k =0;k<emergencyUnits.size();k++) {
						if(emergencyUnits.get(k).getLocation().getX()==i&& emergencyUnits.get(k).getLocation().getY()==j) {
							Unit u =emergencyUnits.get(k);
							String g="";
							if (u instanceof Ambulance) {
								Citizen c = (Citizen) u.getTarget();
								g =g+ "ID: " + u.getUnitID() + "\nType: Ambulance" + "\nLocation: "
										+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
										+ u.getStepsPerCycle() + "\nState: " + u.getState();
								if (c != null) {
									g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
											+ c.getLocation().getY();
								}
							}
							if (u instanceof DiseaseControlUnit) {
								Citizen c = (Citizen) u.getTarget();
								g =g+ "ID: " + u.getUnitID() + "\nType: Disease Control Unit" + "\nLocation: "
										+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
										+ u.getStepsPerCycle() + "\nState: " + u.getState();
								if (c != null) {
									g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
											+ c.getLocation().getY();
								}
							}
							if (u instanceof FireTruck) {
								ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
								g = g+ "ID: " + u.getUnitID() + "\nType: Fire Truck" + "\nLocation: "
										+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
										+ u.getStepsPerCycle() + "\nState: " + u.getState();
								if (b != null) {
									g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
											+ b.getLocation().getY();
								}
							}
							
							if (u instanceof GasControlUnit) {
								ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
								g =g+ "ID: " + u.getUnitID() + "\nType: Gas control Unit" + "\nLocation: "
										+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
										+ u.getStepsPerCycle() + "\nState: " + u.getState();
								if (b != null) {
									g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
											+ b.getLocation().getY();
								}}
							if (u instanceof Evacuator) {
								ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
								g =g+ "ID: " + u.getUnitID() + "\nType: Evacuator" + "\nLocation: "
										+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
										+ u.getStepsPerCycle() + "\nState: " + u.getState() + "\nNo.of Passengers: "
										+ ((Evacuator) u).getPassengers().size() + "\nPassengers: ";
								for (int l = 0; l < ((Evacuator) u).getPassengers().size(); l++) {
									Citizen c = (Citizen) ((Evacuator) u).getPassengers().get(j);
									g = g + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
											+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge()
											+ "\nHP: " + c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: "
											+ c.getToxicity() + "\nState: " + c.getState();
								}
								if (b != null) {
									g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
											+ b.getLocation().getY();
								}
							}
							window.updateunitinfo(g);
						}
					}
				}
			}
		}
		
		
		for (int i = 0; i < window.getArr().length; i++) {
			for (int j = 0; j < window.getArr()[i].length; j++) {
				if (a == window.getArr()[i][j]) {
					bcclick = true;
					x = i;
					y = j;
					gettheobject(i, j);
					window.updateBCinfo(find(i, j));
				}
			}
		}

		for (int w = 0; w < window.getUnits().size(); w++) {
			if (a == window.getUnits().get(w)) {
					Unit u = (Unit) emergencyUnits.get(w);

					if (u instanceof Ambulance) {
						Citizen c = (Citizen) u.getTarget();
						String g = "ID: " + u.getUnitID() + "\nType: Ambulance" + "\nLocation: "
								+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
								+ u.getStepsPerCycle() + "\nState: " + u.getState();
						if (c != null) {
							g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
									+ c.getLocation().getY();
						}

						window.updateunitinfo(g);

						uclick = true;
						if (bcclick = true) {
							try {
								u.respond(gettheobject(x, y));
								uclick = false;
								bcclick = false;

							} catch (IncompatibleTargetException h) {

								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot send an Ambulance to a building!", JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							} catch (CannotTreatException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot help this target with an Ambulance", JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							}
						}

					}


					if (u instanceof DiseaseControlUnit) {
						Citizen c = (Citizen) u.getTarget();
						String g = "ID: " + u.getUnitID() + "\nType: Disease Control Unit" + "\nLocation: "
								+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
								+ u.getStepsPerCycle() + "\nState: " + u.getState();
						if (c != null) {
							g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
									+ c.getLocation().getY();
						}

						window.updateunitinfo(g);
						uclick = true;
						if (bcclick = true) {
							try {
								u.respond(gettheobject(x, y));
								uclick = false;
								bcclick = false;

							} catch (IncompatibleTargetException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot send a Disease Control Unit to a building",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							} catch (CannotTreatException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot help this target with a Disease Control Unit",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							}
						}
					}
					if (u instanceof FireTruck) {
						ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
						String g = "ID: " + u.getUnitID() + "\nType: Fire Truck" + "\nLocation: "
								+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
								+ u.getStepsPerCycle() + "\nState: " + u.getState();
						if (b != null) {
							g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
									+ b.getLocation().getY();
						}

						window.updateunitinfo(g);
						uclick = true;
						if (bcclick = true) {
							try {
								u.respond(gettheobject(x, y));
								uclick = false;
								bcclick = false;

							} catch (IncompatibleTargetException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);
								JLabel l = new JLabel("You cannot send a Fire Truck to a Citizen",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);
							} catch (CannotTreatException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot help this target with a Fire Truck",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							}
						}

					}

					if (u instanceof GasControlUnit) {
						ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
						String g = "ID: " + u.getUnitID() + "\nType: Gas control Unit" + "\nLocation: "
								+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
								+ u.getStepsPerCycle() + "\nState: " + u.getState();
						if (b != null) {
							g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
									+ b.getLocation().getY();
						}

						window.updateunitinfo(g);
						uclick = true;
						if (bcclick = true) {
							try {
								u.respond(gettheobject(x, y));
								uclick = false;
								bcclick = false;

							} catch (IncompatibleTargetException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot send a Gas Control Unit to a Citizen",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);
							} catch (CannotTreatException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot help this target with a Gas Control Unit",
										JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							}
						}

					}

					if (u instanceof Evacuator) {
						ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
						String g = "ID: " + u.getUnitID() + "\nType: Evacuator" + "\nLocation: "
								+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
								+ u.getStepsPerCycle() + "\nState: " + u.getState() + "\nNo.of Passengers: "
								+ ((Evacuator) u).getPassengers().size() + "\nPassengers: ";
						for (int j = 0; j < ((Evacuator) u).getPassengers().size(); j++) {
							Citizen c = (Citizen) ((Evacuator) u).getPassengers().get(j);
							g = g + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
									+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge()
									+ "\nHP: " + c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: "
									+ c.getToxicity() + "\nState: " + c.getState();
						}
						if (b != null) {
							g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
									+ b.getLocation().getY();
						}
						window.updateunitinfo(g);

						uclick = true;
						if (bcclick = true) {

							try {
								u.respond(gettheobject(x, y));
								uclick = false;
								bcclick = false;

							} catch (IncompatibleTargetException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot send an Evacuator to a Citizen", JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							} catch (CannotTreatException h) {
								JDialog d = new JDialog(window);
								d.setTitle("ERROR!!");
								d.setSize(800, 200);
								d.setLocation(300, 250);

								JLabel l = new JLabel("You cannot help this target with an Evacuator", JLabel.CENTER);
								l.setFont(new Font("Centaur", Font.BOLD, 32));
								l.setForeground(Color.RED);
								l.setBackground(Color.BLACK);
								l.setOpaque(true);
								d.add(l);
								d.setVisible(true);

							}

						}
					}
				
			}
		}

		if (a == window.getNextcycle()) {
			engine.nextCycle();

		} else if (a == window.getClose()) {
			window.dispose();
		} else if (a == window.getPlayagain()) {
			window.dispose();
			try {
				new CommandCenter();
			} catch (Exception e1) {

			}
		}
		
		if(a==window.getArr()[0][0]) {
			
			window.updateBCinfo(checkcitizen());
			//System.out.println("gg");
			String g="";
			for(int i=0;i<emergencyUnits.size();i++){
				Unit u = (Unit) emergencyUnits.get(i);
				if(emergencyUnits.get(i).getLocation().getX()==0&&emergencyUnits.get(i).getLocation().getY()==0)
				{
					if(u instanceof Ambulance) {
						
					Citizen c = (Citizen) u.getTarget();
					g = g+"ID: " + u.getUnitID() + "\nType: Ambulance" + "\nLocation: "
							+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
							+ u.getStepsPerCycle() + "\nState: " + u.getState()+"\n";
					if (c != null) {
						g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
								+ c.getLocation().getY()+"\n";
					}
					
				}
					if(u instanceof DiseaseControlUnit) {
					Citizen c = (Citizen) u.getTarget();
					g =g+"ID: " + u.getUnitID() + "\nType: Disease Coontrol Unit" + "\nLocation: "
							+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
							+ u.getStepsPerCycle() + "\nState: " + u.getState()+"\n";
					if (c != null) {
						g = g + "\nTarget: " + c.getName() + "\nTarget's Loation: " + c.getLocation().getX() + ","
								+ c.getLocation().getY()+"\n";
					}
				
				}
					if(u instanceof FireTruck)
					{
					ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
					g =g+ "ID: " + u.getUnitID() + "\nType: Fire Truck" + "\nLocation: "
							+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
							+ u.getStepsPerCycle() + "\nState: " + u.getState()+"\n";
					if (b != null) {
						g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
								+ b.getLocation().getY()+"\n";
					}
					
					}
					if(u instanceof GasControlUnit)
					{
					ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
					g = g+ "ID: " + u.getUnitID() + "\nType: Gas Control Unit" + "\nLocation: "
							+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
							+ u.getStepsPerCycle() + "\nState: " + u.getState()+"\n";
					if (b != null) {
						g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
								+ b.getLocation().getY()+"\n";
					}
					
					}
					if(u instanceof Evacuator){
					ResidentialBuilding b = (ResidentialBuilding) u.getTarget();
					String m="";
					g = g+ "ID: " + u.getUnitID() + "\nType: Evacuator" + "\nLocation: "
							+ u.getLocation().getX() + "," + u.getLocation().getY() + "\nSteps Per Cycle: "
							+ u.getStepsPerCycle() + "\nState: " + u.getState() + "\nNo.of Passengers: "
							+ ((Evacuator) u).getPassengers().size() + "\nPassengers: ";
					for (int j = 0; j < ((Evacuator) u).getPassengers().size(); j++) {
						Citizen c = (Citizen) ((Evacuator) u).getPassengers().get(j);
						g = g + "\nName: " + c.getName() + "\nID: " + c.getNationalID() + "\nLoction: "
								+ c.getLocation().getX() + "," + c.getLocation().getY() + "\nAge: " + c.getAge()
								+ "\nHP: " + c.getHp() + "\nBlood Loss: " + c.getBloodLoss() + "\nToxicity: "
								+ c.getToxicity() + "\nState: " + c.getState()+"\n";
					}
					
			
					
					
					if (b != null) {
						g = g + "\nTarget: Building\nTarget's Location: " + b.getLocation().getX() + ","
								+ b.getLocation().getY();
					}
					
					}
				}
				
			}
			window.updateunitinfo(g);
		}
	}

	public ArrayList<ResidentialBuilding> getVisibleBuildings() {
		return visibleBuildings;
	}

	public ArrayList<Citizen> getVisibleCitizens() {
		return visibleCitizens;
	}

	public ArrayList<Unit> getEmergencyUnits() {
		return emergencyUnits;
	}

}
