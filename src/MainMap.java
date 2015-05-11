import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
*
* This is main class for display GUI on map, and then display map on screen
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class MainMap {

	private double ZOOM_FACTOR = 1.3;
	private double PAN_FRACTION = 0.2;
	private int WINDOW_SIZE = 900;

	private JFrame frame;
	private JComponent drawing;
	private JTextArea textOutputArea;

	private NodeSet nodes;
	private RoadSet roads;
	private SegmentSet segs;

	private MapNode startSelected, goalSelected;
	private boolean aStar;
	private Set<MapSegment> route;
	private Set<MapNode> articulationPts;
	private boolean articulationPt;

	private Location origin;

	double scale;
	double[] boundary;
	double westBoundary;
    double eastBoundary;
    double southBoundary;
    double northBoundary;

	private Point moveStartPt;
	private Location moveOrigin;

	AutoSuggestionTextField<String> textField;

	public MainMap(String nodeFile, String roadFile, String segFile) {
		initialNode(nodeFile);
		initialRoad(roadFile);
		initialSegment(segFile, roads, nodes);

		boundary = nodes.getBoundaries();

		GUI();

		textOutputArea.append("Using keyboard to implement following functions: \n");
    	textOutputArea.append("1) UP, DOWN, LEFT, RIGHT to moving the map. \n");
    	textOutputArea.append("2) MINUS, EQUAL or use mouse wheel to zooming the map . \n\n");
    	textOutputArea.append("Loading completed. \n");
    	textOutputArea.append("\n");
    	textOutputArea.append("Double click to choose startNode and goalNode.\n");

    	setBoundaries();

		drawing.setBorder(BorderFactory.createLineBorder(Color.black));
		drawing.repaint();
	}

	@SuppressWarnings("serial")
	private void GUI() {
		frame = new JFrame("AKL_MAP_SYSTEM V2 developed by yuantian");
		frame.setSize((int) (WINDOW_SIZE * 1.25), WINDOW_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//display area
		drawing = new JComponent() {
			protected void paintComponent(Graphics g) {
				redraw(g);
			}
		};
		frame.add(drawing, BorderLayout.CENTER);

		//button area
		JPanel panel = new JPanel();
		frame.add(panel, BorderLayout.NORTH);

		//text output area
		textOutputArea = new JTextArea(10, 100);
		textOutputArea.setEditable(false);
		JScrollPane jsp = new JScrollPane(textOutputArea);
		frame.add(jsp, BorderLayout.SOUTH);

		//A* search
		JButton AStarButton = new JButton("A* Search");
		panel.add(AStarButton);
		AStarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (goalSelected == null) {
					textOutputArea.setText("Please select node: ");
				} else {
					AStar();
					drawing.repaint();
				}
			}
		});

		//articulation points
		JButton APtsButton = new JButton("Articulation Pts");
		panel.add(APtsButton);
		APtsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				articulationPt();
				drawing.repaint();
			}
		});

		//add reset button allow user to restart the map
		JButton resetButton = new JButton("Reset");
		panel.add(resetButton);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				reset();
			}
		});

		//add quit button allow user directly quit the application
		JButton quitButton = new JButton("Quit");
		panel.add(quitButton);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0);
			}
		});

		//search bar
		//using method provide by David Wang
		panel.add(new JLabel("Search: "));
		textField = new AutoSuggestionTextField<String>();
		textField.setPreferredSize(new Dimension(100, 30));
		textField.setAutoSuggestor(roads);
		textField.setMapToDraw(drawing);
		textField.setSuggestionListener(new SuggestionListener<String>() {
			public void onSuggestionSelected(String item){
				drawing.repaint();
			}

			public void onEnter(String query) {
				drawing.repaint();
			}

			public void onDeselect() {
			}
		});
		panel.add(textField);

		//select the articulation pts -> double click
		drawing.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					if(articulationPt){
						articulationPt = false;
					}

					MapNode temp = findNode(e.getPoint());

					if(temp != null) {
						if(startSelected == null) {
							startSelected = temp;
							aStar = false;
						}
						else if(startSelected != null && goalSelected == null) {
							goalSelected = temp;
						}
						else{
							startSelected = temp;
							goalSelected = null;
							aStar = false;
						}
					}
					drawing.repaint();
				}
			}
			public void mousePressed(MouseEvent e) {
				moveStartPt = e.getPoint();
				moveOrigin = origin;
			}
		});

		//implement mouse function to doing zoom in and zoom out
		drawing.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				Point pt = e.getPoint();
				double clicks = e.getWheelRotation();

				if(clicks > 0) {
					zoom(1 / ZOOM_FACTOR / clicks, pt.x, pt.y);
				}
				else if(clicks < 0) {
					zoom((-1 * clicks) * ZOOM_FACTOR, pt.x, pt.y);
				}
			}
		});

		//implement moving map
		drawing.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				Point pt1 = e.getPoint();
				Point pt2 = new Point(moveStartPt.x - pt1.x, moveStartPt.y - pt1.y);
				origin = Location.newFromPoint(pt2, moveOrigin, scale);
				drawing.repaint();
			}

			public void mouseMoved(MouseEvent e) {
			}
		});

		/**
		 * Following code to respond to map when user press the key on keyboard
		 * Like:
		 * 1) UP -> moving up
		 * 2) DOWN -> moving down
		 * 3) LEFT -> moving left
		 * 4) RIGHT -> moving right
		 * 5) MINUS -> zooming out
		 * 6) EQUALS -> zooming in
		 *
		 * Link: http://www.java2s.com/Code/JavaAPI/javax.swing/InputMapputKeyStrokekeyStrokeObjectactionMapKey.htm
		 */

		InputMap inputMap = drawing.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = drawing.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke("UP"), "panUp");
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "panDown");
		inputMap.put(KeyStroke.getKeyStroke("LEFT"), "panLeft");
		inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "panRight");
		inputMap.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
		inputMap.put(KeyStroke.getKeyStroke("EQUALS"), "zoomIn");

		actionMap.put("panUp", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panUp();
			}
		});

		actionMap.put("panDown", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panDown();
			}
		});

		actionMap.put("panLeft", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panLeft();
			}
		});

		actionMap.put("panRight", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				panRight();
			}
		});

		actionMap.put("zoomIn", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				zoom(ZOOM_FACTOR);
			}
		});

		actionMap.put("zoomOut", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				zoom(1 / ZOOM_FACTOR);
			}
		});

		frame.setVisible(true);
	}

	private void initialNode(String nodeFile) {
		nodes = new NodeSet(nodeFile);
	}

	private void initialRoad(String roadFile) {
		roads = new RoadSet(roadFile);
	}

	private void initialSegment(String segFile, RoadSet roads, NodeSet nodes) {
		segs = new SegmentSet(segFile, roads, nodes);
	}

	private void setBoundaries() {
		westBoundary = boundary[0];
	    eastBoundary = boundary[1];
	    southBoundary = boundary[2];
	    northBoundary = boundary[3];

	    origin = new Location(westBoundary, northBoundary);

	    int width = drawing.getWidth(); //width = eastBoundary - westBoundary
		int height = drawing.getHeight(); //height = northBoundary - southBoundary

	    scale = Math.min(width / (eastBoundary - westBoundary), height / (northBoundary - southBoundary));
	}

	private MapNode findNode(Point pt) {
		Location location = Location.newFromPoint(pt, origin, scale);
		return nodes.find(location, scale);
	}

	private void redraw(Graphics g) {
		Set<MapSegment> neighbourSegs = nodes.draw(g, origin, scale, drawing.getWidth(), drawing.getHeight(), new Color(0, 0, 255));

		for(MapSegment e : neighbourSegs) {
			e.drawRoad(g, origin, scale);
		}

		if(startSelected != null){
			startSelected.drawNode(g, origin, scale, Color.red, MapNode.radius * 2);
		}

		if(goalSelected != null) {
			goalSelected.drawNode(g, origin, scale, Color.red, MapNode.radius * 2);
		}

		if(aStar) {
			drawSpecialSegs(g, origin, scale, route, 3, Color.red);
			Set<MapRoad> road = new HashSet<MapRoad>();
			double totalLength = 0;

			for(MapSegment seg: route) {
				totalLength += seg.getRoadLength();
				MapRoad roadTemp = roads.getRoadById(seg.getRoadId());

				if(road.contains(roadTemp)){
					roadTemp.setLength(roadTemp.getLength() + seg.getRoadLength());
				}
				else{
					roadTemp.setLength(seg.getRoadLength());
					road.add(roadTemp);
				}
			}

			String string = "";
			Set<String> name = new HashSet<String>();

			for(MapRoad tRoad: road) {
				if (!name.contains(tRoad.getLabel())) {
					name.add(tRoad.getLabel());
					string = string + tRoad.getLabel() + " Length: " + tRoad.getLength() + "\n";
				}
			}

			string = string + "\n" + "Total length: " + totalLength + "\n";
			textOutputArea.setText(string);
		}

		if(articulationPt) {
			for(MapNode n: articulationPts) {
				n.drawNode(g, origin, scale, Color.yellow, MapNode.radius * 2);
			}
		}

		String queryRoadName = textField.getCurrentQuery();

		if(!queryRoadName.isEmpty()) {
			textOutputArea.append("Road Query Name:" + queryRoadName + "\n");
		}

		Set<MapRoad> selectedRoads = roads.getRoadByName(queryRoadName);

		if(selectedRoads != null && selectedRoads.size() > 0) {
			textOutputArea.append("GGood! " + selectedRoads.size() + " Roads with name " + queryRoadName + " found! \n");
			for(MapRoad road : selectedRoads) {
				drawSpecialSegs(g, origin, scale, road.getSegs(), 3, Color.green);
			}
		}
	}

	//zoom method
	private void zoom(double zoomFactor, int x, int y) {
		Location m = Location.newFromPoint(new Point(x, y), origin, scale);
		Location newOrigin = new Location((m.x * (1 - 1 / zoomFactor) + origin.x / zoomFactor), (m.y * (1 - 1 / zoomFactor) + origin.y / zoomFactor));

		origin = newOrigin;
		scale *= zoomFactor;
		drawing.repaint();
	}

	private void zoom(double zoomFactor) {
		int x = drawing.getWidth() / 2;
		int y = drawing.getHeight() / 2;
		zoom(zoomFactor, x, y);
	}

	//move left method
    private void panLeft() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x + newOrigin, origin.y);
    	drawing.repaint();
    }

    //move right method
    private void panRight() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x - newOrigin, origin.y);
    	drawing.repaint();
    }

    //move up method
    private void panUp() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x, origin.y - newOrigin);
    	drawing.repaint();
    }

    //move down method
    private void panDown() {
    	double newOrigin = WINDOW_SIZE * PAN_FRACTION / scale;
    	origin = new Location(origin.x, origin.y + newOrigin);
    	drawing.repaint();
    }

	public void setGraphics(Graphics g, int width, Color color) {
		((Graphics2D) g).setStroke(new BasicStroke(width));
		((Graphics2D) g).setColor(color);
	}

	public void setGraphics(Graphics g, Stroke stroke, Color color) {
		((Graphics2D) g).setStroke(stroke);
		((Graphics2D) g).setColor(color);
	}

	//reset method
	private void reset() {
		setBoundaries();
		startSelected = null;
		goalSelected = null;
		textField.removeAllItems();

		textOutputArea.setText(" ");
		drawing.repaint();
		aStar = false;
		articulationPt = false;
	}

	//A* method
	private void AStar() {
		new AStar(nodes, segs, roads, startSelected, goalSelected);
		route = new HashSet<MapSegment>();
		MapSegment segTemp = goalSelected.getSegFrom();
		MapNode nodeTemp = goalSelected;

		while(nodeTemp != startSelected) {
			route.add(segTemp);
			nodeTemp = nodeTemp.getPathFrom();
			segTemp = nodeTemp.getSegFrom();
		}

		aStar = true;
	}

	//articulation points method
	private void articulationPt() {
		articulationPt = false;
		startSelected = null;
		goalSelected = null;

		articulationPts = new HashSet<MapNode>();
		ArtPts apt = new ArtPts(nodes, segs, roads);
		articulationPts = apt.findPt();
		articulationPt = true;
	}

	public void drawSpecialSegs(Graphics g, Location origin, double scale, Set<MapSegment> edgesToDraw, int width, Color color) {
		Stroke previousStroke = ((Graphics2D) g).getStroke();
		Color previousColor = ((Graphics2D) g).getColor();
		setGraphics(g, width, color);

		for (MapSegment edge : edgesToDraw) {
			edge.drawRoad(g, origin, scale);
		}

		setGraphics(g, previousStroke, previousColor);
	}

	public static void main(String[] args) {
		String nodeFile = "./src/data/nodeID-lat-lon.tab";
		String roadFile = "./src/data/roadID-roadInfo.tab";
		String segFile = "./src/data/roadSeg-roadID-length-nodeID-nodeID-coords.tab";
		//String resFile = "./src/data/restrictions.tab";
		//String polyFile = "./src/data/polygon-shapes.mp";

		@SuppressWarnings("unused")
		MainMap map = new MainMap(nodeFile, roadFile, segFile);
	}
}