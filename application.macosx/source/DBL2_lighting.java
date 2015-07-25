import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import heronarts.lx.*; 
import heronarts.lx.audio.*; 
import heronarts.lx.color.*; 
import heronarts.lx.model.*; 
import heronarts.lx.modulator.*; 
import heronarts.lx.parameter.*; 
import heronarts.lx.pattern.*; 
import heronarts.lx.transition.*; 
import heronarts.p2lx.*; 
import heronarts.p2lx.ui.*; 
import heronarts.p2lx.ui.control.*; 
import ddf.minim.*; 
import processing.opengl.*; 
import java.awt.Dimension; 
import java.awt.Toolkit; 
import java.io.*; 
import java.nio.file.*; 
import java.util.*; 
import java.io.*; 
import java.nio.file.*; 
import java.util.*; 
import java.awt.Color; 
import org.jcolorbrewer.ColorBrewer; 
import java.nio.*; 
import java.util.Arrays; 

import heronarts.p2lx.font.*; 
import heronarts.lx.transition.*; 
import heronarts.lx.transform.*; 
import heronarts.p2lx.ui.component.*; 
import heronarts.lx.pattern.*; 
import netP5.*; 
import heronarts.lx.model.*; 
import heronarts.p2lx.*; 
import heronarts.lx.midi.device.*; 
import heronarts.p2lx.ui.control.*; 
import org.jcolorbrewer.ui.*; 
import oscP5.*; 
import demo.*; 
import heronarts.lx.modulator.*; 
import org.jcolorbrewer.*; 
import heronarts.lx.output.*; 
import heronarts.lx.midi.*; 
import heronarts.lx.effect.*; 
import heronarts.lx.color.*; 
import heronarts.lx.parameter.*; 
import heronarts.p2lx.video.*; 
import heronarts.p2lx.ui.*; 
import heronarts.lx.*; 
import heronarts.lx.audio.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DBL2_lighting extends PApplet {

// Get all our imports out of the way


















//set screen size
Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
final int VIEWPORT_WIDTH = 800; // for fullscreen, replace with (int)screenSize.getWidth();
final int VIEWPORT_HEIGHT = 600; //for fullscreen, replace with (int)screenSize.getHeight();

HueCyclePalette palette;

// Let's work in inches
final static int INCHES = 1;
final static int FEET = 12*INCHES;
float[] hsb = new float[3];

// Top-level, we have a model and a P2LX instance
static Model model;
P2LX lx;

// Target frame rate
int FPS_TARGET = 60;  



  // Always draw FPS meter
public void drawFPS() {  
  fill(0xff999999);
  textSize(9);
  textAlign(LEFT, BASELINE);
  text("FPS: " + ((int) (frameRate*10)) / 10.f + " / " + "60" + " (-/+)", 4, height-4);
}

/**
 * Set up models etc for whole package (Processing thing).
*/
public void setup() {
  
  //set Processing color mode to HSB instead of RGB
  colorMode(HSB);
  
  //set screen size
  size(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, OPENGL);
  frame.setResizable(true);

  //not necessary, uncomment and play with it if the frame has issues
  //frame.setSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
  
  //framerates
  frameRate(FPS_TARGET);
  noSmooth();
  
  //Which bar selection to use. For the hackathon we're using the full_brain but there are a few others
  // for other reasons (single modules, reduced-bar-version, etc)
  String bar_selection = "Full_Brain";

  //Actually builds the model (per mappings.pde)
  model = buildTheBrain(bar_selection);
  
  //initialize node-bar connections in model
  for (String barname : model.barmap.keySet()){
    Bar bar = model.barmap.get(barname);
    bar.initialize_model_connections();
  }
  for (String nodename : model.nodemap.keySet()){
    Node node = model.nodemap.get(nodename);
    node.initialize_model_connections();
  }
  for (String barname : model.barmap.keySet()){
    Bar bar = model.barmap.get(barname);
    bar.initialize_model_connections();
  }
  for (String nodename : model.nodemap.keySet()){
    Node node = model.nodemap.get(nodename);
    node.initialize_model_connections();
  }
  for (String barname : model.barmap.keySet()){
    Bar bar = model.barmap.get(barname);
    bar.initialize_model_connections();
  }
  for (String nodename : model.nodemap.keySet()){
    Node node = model.nodemap.get(nodename);
    node.initialize_model_connections();
  }
  println("Total # pixels in model: " + model.points.size());
  
  // Create the P2LX engine
  lx = new P2LX(this, model);
  palette = new HueCyclePalette(lx);
  palette.hueMode.setValue(LXPalette.HUE_MODE_CYCLE);
  lx.enableKeyboardTempo(); 
  lx.engine.getChannel(0).setPalette(palette);
  lx.engine.addLoopTask(palette);
  LXEngine engine = lx.engine;
  
  lx.engine.framesPerSecond.setValue(FPS_TARGET);
  lx.engine.setThreaded(false);
  // Set the patterns
  engine.setPatterns(new LXPattern[] {
    new TestImagePattern(lx),
    new HelloWorldPattern(lx),
    new GradientPattern(lx),
    new TestHuePattern(lx),
    new TestHemispheres(lx),
    new RandomBarFades(lx),
    new SuperBasicLightningStrikes(lx),
    new RainbowBarrelRoll(lx),
    new EQTesting(lx),
    new LayerDemoPattern(lx),
    new CircleBounce(lx),
    new CirclesBounce(lx),
    new SampleNodeTraversalWithFade(lx),
    new SampleNodeTraversal(lx),
    new TestXPattern(lx),
    new IteratorTestPattern(lx),
    new TestBarPattern(lx),
  });
  println("Initialized patterns");

  //adjust this if you want to play with the initial camera setting.
  /*
  lx.ui.addLayer(
    // Camera layer
    new UI3dContext(lx.ui)
      .setCenter(model.cx, model.cy, model.cz)
      .setRadius(290).addComponent(new UIBrainComponent())
  );
  */
  
  // Add UI elements
  lx.ui.addLayer(
    // A camera layer makes an OpenGL layer that we can easily 
    // pivot around with the mouse
    new UI3dContext(lx.ui) {
      protected void beforeDraw(UI ui, PGraphics pg) {
        // Let's add lighting and depth-testing to our 3-D simulation
        pointLight(0, 0, 40, model.cx, model.cy, -20*FEET);
        pointLight(0, 0, 50, model.cx, model.yMax + 10*FEET, model.cz);
        pointLight(0, 0, 20, model.cx, model.yMin - 10*FEET, model.cz);
        //hint(ENABLE_DEPTH_TEST);
      }
      protected void afterDraw(UI ui, PGraphics pg) {
        // Turn off the lights and kill depth testing before the 2D layers
        noLights();
        hint(DISABLE_DEPTH_TEST);
      } 
    }
  
  // Let's look at the center of our model
  //.setCenter(5,5,5)
  
  // Let's position our eye some distance away
  .setRadius(40*FEET)
    
  // And look at it from a bit of an angle
  //.setTheta(PI/24)
  //.setPhi(PI/24)
    
  //.setRotateVelocity(12*PI)
  //.setRotateAcceleration(3*PI)
    
  // Let's add a point cloud of our animation points
  .addComponent(new UIBrainComponent())
    
  // And a custom UI object of our own
  // .addComponent(new UIWalls())
  );
  
  // A basic built-in 2-D control for a channel
  lx.ui.addLayer(new UIChannelControl(lx.ui, lx.engine.getChannel(0), 4, 4));
  lx.ui.addLayer(new UIEngineControl(lx.ui, 4, 326));
  //lx.ui.addLayer(new UIComponentsDemo(lx.ui, width-144, 4));
  lx.ui.addLayer(new UIGlobalControl(lx.ui, width-144, 4));

  // output to controllers
  // buildOutputs();

  lx.engine.framesPerSecond.setValue(FPS_TARGET);
  lx.engine.setThreaded(false);
}


/**
 * Processing's draw loop.
*/
public void draw() {
  // Wipe the frame...
  background(40);
  int[] sendColors = lx.getColors();  
  long gammaStart = System.nanoTime();
  // Gamma correction here. Apply a cubic to the brightness
  // for better representation of dynamic range
  
  drawFPS();

  for (int i = 0; i < sendColors.length; ++i) {
    LXColor.RGBtoHSB(sendColors[i], hsb);
    float b = hsb[2];
    sendColors[i] = lx.hsb(360.f*hsb[0], 100.f*hsb[1], 100.f*(b*b*b));
  }

  // ...and everything else is handled by P2LX!
}



/**
 * Creates a custom pattern class for writing patterns onto the brain model 
 * Don't modify unless you know what you're doing.
*/
public static abstract class BrainPattern extends LXPattern {
  protected Model model;
  
  protected BrainPattern(LX lx) {
    super(lx);
    this.model = (Model) lx.model;
  }
}



//import processing.data.Table;

//Builds the brain model
//BEWARE. Lots of csvs and whatnot.
//It's uglier than sin, but the brain is complicated, internally redundant, and not always heirarchical.
//It works.
public Model buildTheBrain(String bar_selection_identifier) { 
  
  String mapping_data_location="mapping_datasets/"+bar_selection_identifier+"/";
  
  SortedMap<String, List<float[]>> barlists = new TreeMap<String, List<float[]>>();
  SortedMap<String, Bar> bars = new TreeMap<String, Bar>();
  SortedMap<String, Node> nodes = new TreeMap<String, Node>();
  boolean newbar;
  boolean newnode;


  //Map the pixels to individual LEDs and in the process declare the physical bars.
  //As of 15/6/1 the physical bars are the only things that don't have their own declaration table
  //TODO: This is now mostly handled by the Bar class loading, so clean it up and get rid of the unnecessary parts.
  Table pixelmapping = loadTable(mapping_data_location+"pixel_mapping.csv", "header");
  List<float[]> bar_for_this_particular_led;
  Set barnames = new HashSet();
  Set nodenames = new HashSet();
  List<String> bars_in_pixel_order = new ArrayList<String>();
  for (processing.data.TableRow row : pixelmapping.rows()) {
    int pixel_num = row.getInt("Pixel_i");
    float x = row.getFloat("X");
    float y = row.getFloat("Y");
    float z = row.getFloat("Z");
    String node1 = row.getString("Node1");
    String node2 = row.getString("Node2");
    String strip_num = row.getString("Strip");
    String bar_name=node1+"-"+node2;
    newbar=barnames.add(bar_name);
    if (newbar){
      bars_in_pixel_order.add(bar_name);
      List<float[]> poince = new ArrayList<float[]>();
      barlists.put(bar_name,poince); 
    }
    bar_for_this_particular_led = barlists.get(bar_name);
    float[] point = new float[]{x,y,z};
    bar_for_this_particular_led.add(point);
  } 
  println("Finished loading pixel_mapping");
  
  
  //Load the node info for the model nodes. (ignores double nodes)
  Table node_csv = loadTable(mapping_data_location+"Model_Node_Info.csv","header");
  

  for (processing.data.TableRow row : node_csv.rows()) {
    String node = row.getString("Node");
    float x = row.getFloat("X");
    float y = row.getFloat("Y");
    float z = row.getFloat("Z");
    String csv_neighbors = row.getString("Neighbor_Nodes");
    String csv_connected_bars = row.getString("Bars");
//    String csv_connected_physical_bars = row.getString("Physical_Bars");
//    String csv_adjacent_physical_nodes = row.getString("Physical_Nodes");
    boolean ground;
    String groundstr = row.getString("Ground");
    String inner_outer = row.getString("Inner_Outer");
    String left_right_mid = row.getString("Left_Right_Mid");
    if (groundstr.equals("1")){
      ground=true;
    }
    else{
      ground=false;
    } 

    //all of those were strings - split by the underscores
    List<String> neighbors = Arrays.asList(csv_neighbors.split("_"));
    List<String> connected_bars = Arrays.asList(csv_connected_bars.split("_"));
    Node nod = new Node(node,x,y,z,connected_bars, neighbors, ground,inner_outer, left_right_mid); 
   
    nodes.put(node,nod);
  }
  println("finished loading model_node_info");
  
  
  //Load the model bar info (which has conveniently abstracted away all of the double node jiggery-pokery)
  Table bars_csv = loadTable(mapping_data_location+"Model_Bar_Info.csv","header");
  
  for (processing.data.TableRow row : bars_csv.rows()) {
    String barname = row.getString("Bar_name");
    float min_x = row.getFloat("Min_X");
    float min_y = row.getFloat("Min_Y");
    float min_z = row.getFloat("Min_Z");
    float max_x = row.getFloat("Max_X");
    float max_y = row.getFloat("Max_Y");
    float max_z = row.getFloat("Max_Z");
    String csv_nods=row.getString("Nodes");
    String module=row.getString("Module");
    String csv_adjacent_nodes = row.getString("Adjacent_Nodes");
    String csv_adjacent_bars = row.getString("Adjacent_Bars");
    String inner_outer = row.getString("Inner_Outer");
    String left_right_mid = row.getString("Left_Right_Mid");
    boolean ground;
    String groundstr = row.getString("Ground");
    if (groundstr.equals("1")){
      ground=true;
    }
    else{
      ground=false;
    } 
    //all of those were strings - split by the underscores
    List<String> nods=Arrays.asList(csv_nods.split("_"));
    List<String> connected_nodes = Arrays.asList(csv_adjacent_nodes.split("_"));
    List<String> connected_bars = Arrays.asList(csv_adjacent_bars.split("_"));
    float current_max_z=-10000;
    List<float[]> usethesepoints = new ArrayList<float[]>();
    usethesepoints = barlists.get(barname);
    Bar barrrrrrr = new Bar(barname,usethesepoints,min_x,min_y,min_z,max_x,max_y,max_z,module,nods,connected_nodes,connected_bars, ground,inner_outer,left_right_mid);
  
    bars.put(barname,barrrrrrr);

  println("Loaded Model bar info");

  }
  Model model = new Model(nodes, bars, bars_in_pixel_order);
  // I can haz brain model.
  return model;
}
  
  
  
  
  
  
  
  
/**
 * This is a model OF A BRAIN!
 */




/**
 * This is the model for the whole brain. It contains four mappings, two of which users should use (Bar and Node)
 * and two which are set up to deal with the physical reality of the actual brain, double bars and double nodes
 * and so on. 
 * @author Alex Maki-Jokela
*/
public static class Model extends LXModel {

  //Note that these are stored in maps, not lists. 
  //Nodes are keyed by their three letter name ("LAB", "YAK", etc)
  //Bars are keyed by the two associated nodes in alphabetical order ("LAB-YAK", etc)
  public final SortedMap<String, Node> nodemap;
  public final SortedMap<String, Bar> barmap;

  public final List<String> bars_in_pixel_order;


  /** 
   * Constructor for Model. The parameters are all fed from Mappings.pde
   * @param nodemap is a mapping of node names to their objects
   * @param barmap is a mapping of bar names to their objects
   * @param bars_in_pixel_order is a list of the physical bars in order of LED indexes which is used for mapping them to LED strings
   */
  public Model(SortedMap<String, Node> nodemap, SortedMap<String, Bar> barmap, List<String> bars_in_pixel_order) {
    super(new Fixture(barmap, bars_in_pixel_order));
    this.nodemap = Collections.unmodifiableSortedMap(nodemap);
    this.barmap = Collections.unmodifiableSortedMap(barmap);
    this.bars_in_pixel_order = Collections.unmodifiableList(bars_in_pixel_order);
  }

  /**
  * Maps the points from the bars into the brain. Note that it iterates through bars_in_pixel_order
  * @param barmap is the map of bars
  * @param bars_in_pixel_order is the list of bar names in order LED indexes
  */
  private static class Fixture extends LXAbstractFixture {
    private Fixture(SortedMap<String, Bar> barmap, List<String> bars_in_pixel_order) {
      for (String barname : bars_in_pixel_order) {
        Bar bar = barmap.get(barname);
        if (bar != null) {
          for (LXPoint p : bar.points) {
            this.points.add(p);
          }
        }
      }
    }
  }

  /**
  * Chooses a random node from the model.
  */
  public Node getRandomNode() {
    //TODO: Instead of declaring a new Random every call, can we just put one at the top outside of everything?
    Random randomized = new Random();
    //TODO: Can this be optimized better? We're using maps so Processing's random function doesn't seem to apply here
    List<String> nodekeys = new ArrayList<String>(this.nodemap.keySet());
    String randomnodekey = nodekeys.get( randomized.nextInt(nodekeys.size()) );
    Node randomnode = this.nodemap.get(randomnodekey);
    return randomnode;
  }


  /**
  * Gets a random bar from the model
  * If I could write getRandomIrishPub and have it work, I would.
  */
  public Bar getRandomBar() {
    //TODO: Instead of declaring a new Random every call, can we just put one at the top outside of everything?
    Random randomized = new Random();
    //TODO: Can this be optimized better? We're using maps so Processing's random function doesn't seem to apply here
    List<String> barkeys = new ArrayList<String>(this.barmap.keySet());
    String randombarkey = barkeys.get( randomized.nextInt(barkeys.size()) );
    Bar randombar = this.barmap.get(randombarkey);
    return randombar;
  }

  /**
  * Returns an arraylist of randomly selected nodes from the model
  * @param num_requested: How many randomly selected nodes does the user want?
  */
  public ArrayList<Node> getRandomNodes(int num_requested) {
    Random randomized = new Random();
    ArrayList<String> returnnodstrs = new ArrayList<String>();
    ArrayList<Node> returnnods = new ArrayList<Node>();
    List<String> nodekeys = new ArrayList<String>(this.nodemap.keySet());
    if (num_requested > nodekeys.size()) {
      num_requested = nodekeys.size();
    }
    while (returnnodstrs.size () < num_requested) {
      String randomnodekey = nodekeys.get( PApplet.parseInt(randomized.nextInt(nodekeys.size())) );
      if (!(Arrays.asList(returnnodstrs).contains(randomnodekey))) {
        returnnodstrs.add(randomnodekey);
      }
    }
    for (String randnod : returnnodstrs) {
      returnnods.add(this.nodemap.get(randnod));
    }
    return returnnods;
  }

  /**
  * Returns an arraylist of randomly selected bars from the model
  * @param num_requested: How many randomly selected bars does the user want?
  */
  public ArrayList<Bar> getRandomBars(int num_requested) {
    Random randomized = new Random();
    ArrayList<String> returnbarstrs = new ArrayList<String>();
    ArrayList<Bar> returnbars = new ArrayList<Bar>();
    List<String> barkeys = new ArrayList<String>(this.nodemap.keySet());
    if (num_requested > barkeys.size()) {
      num_requested = barkeys.size();
    }
    while (returnbarstrs.size () < num_requested) {
      String randombarkey = barkeys.get( PApplet.parseInt(randomized.nextInt(barkeys.size())) );
      if (!(Arrays.asList(returnbarstrs).contains(randombarkey))) {
        returnbarstrs.add(randombarkey);
      }
    }
    for (String randbar : returnbarstrs) {
      returnbars.add(this.barmap.get(randbar));
    }
    return returnbars;
  }
}



/**
 * The Node class is the most useful tool for traversing the brain.
 * @param id: The node id ("BUG", "ZAP", etc)
 * @param x,y,z: The node xyz coords
 * @param ground: Is this node one of the ones on the bottom of the brain?
 * @param adjacent_bar_names: names of bars adjacent to this node
 * @param adjacent_node_names: names of nodes adjacent to this node
 * @param adjacent_bar_names: names of bars adjacent to this node
 * @param id: The node id ("BUG", "ZAP", etc)
*/
public class Node extends LXModel {

  //Node number with module number
  public final String id;

  //Straightforward. If there are multiple physical nodes, this is the xyz from the node with the highest z
  public final float x;
  public final float y;
  public final float z;

  //xyz position of node
  //If it's a double or triple node, returns the coordinates of the highest-z-position instance of the node
  public final boolean ground;
  
  //inner layer or outer layer?
  public final String inner_outer;
  
  //inner layer or outer layer?
  public final String left_right_mid;

  //List of bar IDs connected to node.
  public final List<String> adjacent_bar_names;

  //List of node IDs connected to node.
  public final List<String> adjacent_node_names;


  //Declurrin' some arraylists
  public ArrayList<Bar> adjacent_bars = new ArrayList<Bar>();
  public ArrayList<Node> adjacent_nodes = new ArrayList<Node>();



  
  public Node(String id, float x, float y, float z, List<String> adjacent_bar_names, List<String> adjacent_node_names, boolean ground, String inner_outer, String left_right_mid) {
    this.id=id;
    this.x=x;
    this.y=y;
    this.z=z;
    this.adjacent_bar_names=adjacent_bar_names;
    this.adjacent_node_names = adjacent_node_names;
    this.ground = ground;
    this.inner_outer=inner_outer;
    this.left_right_mid=left_right_mid;
    this.adjacent_bars = new ArrayList<Bar>();
    this.adjacent_nodes = new ArrayList<Node>();
  }


  public void initialize_model_connections(){
    for (String abn : this.adjacent_bar_names){
      this.adjacent_bars.add(model.barmap.get(abn));
    }
    for (String ann : this.adjacent_node_names) {
      this.adjacent_nodes.add(model.nodemap.get(ann));
    }
  }


  /**
  * Returns one adjacent node
  */ 
  public Node random_adjacent_node() {
    String randomnodekey = adjacent_node_names.get( PApplet.parseInt(random(adjacent_node_names.size())) );
    Node returnnod=model.nodemap.get(randomnodekey);
    return returnnod;
  }

  /**
   * Returns an ArrayList of randomly selected adjacent nodes. 
   * @param num_requested: How many random adjacent nodes to return
   */
  public ArrayList<Node> random_adjacent_nodes(int num_requested) {
    ArrayList<String> returnnodstrs = new ArrayList<String>();
    ArrayList<Node> returnnods = new ArrayList<Node>();
    if (num_requested > this.adjacent_node_names.size()) {
      num_requested = this.adjacent_node_names.size();
    }
    while (returnnodstrs.size () < num_requested) {
      String randomnodekey = adjacent_node_names.get( PApplet.parseInt(random(adjacent_node_names.size())) );
      if (!(Arrays.asList(returnnodstrs).contains(randomnodekey))) {
        returnnodstrs.add(randomnodekey);
      }
    }
    for (String randnod : returnnodstrs) {
      returnnods.add(model.nodemap.get(randnod));
    }
    return returnnods;
  }



  /**
  * Returns one adjacent bar
  */ 
  public Bar random_adjacent_bar() {
    String randombarkey = adjacent_bar_names.get( PApplet.parseInt(random(adjacent_bar_names.size())) );
    Bar returnbar=model.barmap.get(randombarkey);
    return returnbar;
  }


  /**
   * Returns an ArrayList of randomly selected adjacent bars. 
   * @param num_requested: How many random adjacent bars to return
   */
  public ArrayList<Bar> random_adjacent_bars(int num_requested) {
    ArrayList<String> returnbarstrs = new ArrayList<String>();
    ArrayList<Bar> returnbars = new ArrayList<Bar>();
    if (num_requested > this.adjacent_bar_names.size()) {
      num_requested = this.adjacent_bar_names.size();
    }
    while (returnbarstrs.size () < num_requested) {
      String randombarkey = adjacent_bar_names.get( PApplet.parseInt(random(adjacent_bar_names.size())) );
      if (!(Arrays.asList(returnbarstrs).contains(randombarkey))) {
        returnbarstrs.add(randombarkey);
      }
    }
    for (String randbar : returnbarstrs) {
      returnbars.add(model.barmap.get(randbar));
    }
    return returnbars;
  }

  //List of adjacent bars
  public ArrayList<Bar> adjacent_bars() {
    ArrayList<Bar> baarrs = new ArrayList<Bar>();
    for (String pnn : this.adjacent_bar_names) {
      baarrs.add(model.barmap.get(pnn));
    }
    return baarrs;
  }
  
  //List of adjacent bars.
  public ArrayList<Node> adjacent_nodes() {
    ArrayList<Node> nods = new ArrayList<Node>();
    for (String pnn : this.adjacent_node_names) {
      nods.add(model.nodemap.get(pnn));
    }
    return nods;
  }

}






/**
 * The Bar class is the second-most-useful tool for traversing the brain.
 * @param id: The bar id ("BUG-ZAP", etc)
 * @param min_x,min_y,min_z: The minimum node xyz coords
 * @param max_x,max_y,max_z: The maximum node xyz coords
 * @param ground: Is this bar one of the ones on the bottom of the brain?
 * @param module_names: Which modules is this bar in? (can be more than one if it's a double-bar)
 * @param node_names: Nodes contained in this bar
 * @param adjacent_bar_names: names of bars adjacent to this node
 * @param adjacent_node_names: names of nodes adjacent to this node
 * @param adjacent_bar_names: names of bars adjacent to this node
*/
public static class Bar extends LXModel {

  //bar name
  public final String id;

  //min and max xyz of bar TODO make these work again
  public final float min_x;
  public final float min_y;
  public final float min_z;
  public final float max_x;
  public final float max_y;
  public final float max_z;

  public final float angle_with_vertical;
  public final float angle_with_horizontal;

  //Is it on the ground? (or bottom of brain)
  public final boolean ground;

  //Inner bar? Outer bar? Mid bar?
  public final String inner_outer_mid;
  
  //Left Hemisphere? Right Hemisphere? Fissure?
  public final String left_right_mid;

  //list of strings of modules that this bar is in.
  public final String module;

  //List of node IDs connected to bar.
  public final List<String> node_names;

  //List of bar IDs connected to bar.
  public final List<String> adjacent_bar_names;

  //List of node IDs connected to bar.
  public final List<String> adjacent_node_names;


  //Bar nodes
  public ArrayList<Node> nodes = new ArrayList<Node>();

  //Adjacent nodes to bar
  public ArrayList<Node> adjacent_nodes = new ArrayList<Node>();

  //Adjacent bars to bar
  public ArrayList<Bar> adjacent_bars = new ArrayList<Bar>();


   
  //This bar is open to the public.
  public Bar(String id, List<float[]> points, float min_x,float min_y,float min_z,float max_x,float max_y,float max_z, String module, List<String> node_names,
  List<String> adjacent_node_names, List<String> adjacent_bar_names, boolean ground, String inner_outer_mid, String left_right_mid) {
    super(new Fixture(points));
    this.id=id;
    this.module=module;
    this.min_x=min_x;
    this.min_y=min_y; 
    this.min_z=min_z;
    this.max_x=max_x;
    this.max_y=max_y;
    this.max_z=max_z;
    float dx = this.max_x-this.min_x;
    float dy = this.max_y-this.min_y;
    float dz = this.max_z-this.min_z;
    float dxy = sqrt(sq(dx)+sq(dy));
    float raw_angle= PVector.angleBetween(new PVector(dx,dy,dz),new PVector(0,0,1));
    this.angle_with_vertical=min(raw_angle,PI-raw_angle);
    this.angle_with_horizontal=PI-this.angle_with_vertical;
    this.inner_outer_mid = inner_outer_mid;
    this.left_right_mid = left_right_mid;
    this.node_names = node_names;
    this.adjacent_node_names=adjacent_node_names;
    this.adjacent_bar_names=adjacent_bar_names;
    this.ground = ground;
    this.nodes = new ArrayList<Node>();
    this.adjacent_bars = new ArrayList<Bar>();
    this.adjacent_nodes = new ArrayList<Node>();
  }


   private static class Fixture extends LXAbstractFixture {
    private Fixture(List<float[]> points) {
      for (float[] p : points ) {
        LXPoint point=new LXPoint(p[0], p[1], p[2]);
        this.points.add(point);
      }
    }
  }

  public void initialize_model_connections(){
    for (String nn : this.node_names) {
      this.nodes.add(model.nodemap.get(nn));
    }
    for (String abn : this.adjacent_bar_names){
      this.adjacent_bars.add(model.barmap.get(abn));
    }
    for (String ann : this.adjacent_node_names) {
      this.adjacent_nodes.add(model.nodemap.get(ann));
    }
  }

  //List of adjacent bars
  public ArrayList<Bar> adjacent_bars() {
    ArrayList<Bar> adj_bars = new ArrayList<Bar>();
    for (String abn : this.adjacent_bar_names) {
      adj_bars.add(model.barmap.get(abn));
    }
    return adj_bars;
  }

  //Returns angle between bars. Bars must be adjacent
  //in radians
  public float angle_with_bar(Bar other_bar){
    if (!(this.adjacent_bars.contains(other_bar))){
      throw new IllegalArgumentException("Bars must be adjacent!");
    }
    return angleBetweenTwoBars(this,other_bar);
  }

}


/**
* Returns a list of LXPoints between two adjacent nodes, in order.
* e.g. if you wanted to get the nodes in order from ZAP to BUG (reverse alphabetical order) this is what you'd use
* reminder: by default the points always go in alphabetical order from one node to another
* returns null if the nodes aren't adjacent.
* @param node1: Start node
* @param node2: End node
*/
public static List<LXPoint> nodeToNodePoints(Node node1, Node node2) {
  String node1name = node1.id;
  String node2name = node2.id;
  int reverse_order = node1name.compareTo(node2name); //is this going in reverse order? 
  String barname;
  if (reverse_order<0) {
    barname = node1name + "-" + node2name;
  } else {
    barname = node2name + "-" + node1name;
  }
  Bar ze_bar = model.barmap.get(barname);

  if (ze_bar == null) { //the bar doesnt exist (non adjacent nodes etc)
    throw new IllegalArgumentException("Nodes must be adjacent!");
  } else {
    if (reverse_order>0) {
      List<LXPoint> zebarpoints = new ArrayList(ze_bar.points);
      Collections.reverse(zebarpoints);
      return zebarpoints;
    } else {
      return ze_bar.points;
    }
  }
}



/**
 * Given two nodes, see if they form a bar.
 * Simple but useful.
 * @param node1: a node
 * @param node2: another node.
*/
public static boolean twoNodesMakeABar(Node node1, Node node2){
  String node1name=node1.id;
  String node2name=node2.id;
  int reverse_order = node1name.compareTo(node2name);
  String barname;
  if (reverse_order<0) {
    barname = node1name + "-" + node2name;
  } else {
    barname = node2name + "-" + node1name;
  }
  if (model.barmap.keySet().contains(barname)){
    return true;
  }
  return false;
}



/**
 * Given two bars with a common node, find that node. Bars must be adjacent.
 * Simple but useful.
 * @param bar1: a bar
 * @param bar2: a connected bar
*/
public static Node sharedNode(Bar bar1, Bar bar2){
  List<Node> allnodes = new ArrayList<Node>();
  for (Node n : bar1.nodes){
    allnodes.add(n);
  }
  for (Node n : bar2.nodes){
    allnodes.add(n);
  }
  for (Node n : allnodes) {
    if (bar1.nodes.contains(n) && bar2.nodes.contains(n)) {
      return n;
    }
  }
  return null; //no matches :(
}

/**
 * Given a bar and a node in that bar, gets the other node from that bar.
 * Simple but useful.
 * @param bar: a bar
 * @param node: a node in that bar
*/
public static Node otherNode(Bar bar, Node node){
  if (bar.nodes.contains(node)){
    for (Node n : bar.nodes){
      if (!(n.id.equals(node.id))){
        return n;
      }
    } 
    throw new IllegalArgumentException("Something is wrong with the bar model.");
  }
  else{
    throw new IllegalArgumentException("Node must be in bar");
  }
}

/**
 * Gets the angle formed by two bars. They must be adjacent to each other.
 * @param Bar1: First bar
 * @param Bar2: Second bar
*/
public static float angleBetweenTwoBars(Bar bar1, Bar bar2){
  if (bar1.adjacent_bars.contains(bar2)){
    Node common_node = sharedNode(bar1,bar2);
    Node node1 = otherNode(bar1,common_node);
    Node node3 = otherNode(bar2,common_node);
    return angleBetweenThreeNodes(node1,common_node,node3);
  } else {
    throw new IllegalArgumentException("Bars must be adjacent!");
  }
}

/**
 * Gets the angle formed by three nodes. They must be adjacent to each other and connected via a bar.
 * @param Node1: The first node
 * @param Node2: The second node (the one where the angle is formed)
 * @param Node3: The third node
*/
public static float angleBetweenThreeNodes(Node node1,Node node2,Node node3){
  if (twoNodesMakeABar(node1,node2) && twoNodesMakeABar(node2,node3)){
    float dx1=node1.x-node2.x;
    float dy1=node1.y-node2.y;
    float dz1=node1.z-node2.z;
    float dx2=node3.x-node2.x;
    float dy2=node3.y-node2.y;
    float dz2=node3.z-node2.z;
    PVector vect1=new PVector(dx1,dy1,dz1);
    PVector vect2=new PVector(dx2,dy2,dz2);
    return PVector.angleBetween(vect1,vect2);
  } else{
    throw new IllegalArgumentException("Nodes must be adjacent!");
  }
}


/**
 * Class for mapping images onto the brain.
 * Operates by doing all the math for which pixels in the image map to which pixels on the brain, once
 * Then shifts things around by changing the pixels in the image.
 * TODO: Could use some optimization magic. Does unkind things to the framerate.
 * @param imagecolors is a Processing PImage which stores the image
 * @param cartesian_canvas defines what coordinate system the image gets mapped to
 * @param imagedims is the dimensions of the image in pixels
 * @param compress_pct compresses the image by a certain percent to improve performance.  Will vary by image and machine.
*/ 
public class MentalImage {

  PImage imagecolors;
  String cartesian_canvas;
  int[] imagedims;
  
  SortedMap<Integer, int[]> pixel_to_pixel = new TreeMap<Integer, int[]>();
  SortedMap<Integer, float[]> led_colors = new TreeMap<Integer, float[]>();

  //Constructor for class
  public MentalImage(String imagepath, String cartesian_canvas, int compress_pct){
      this.imagecolors = loadImage(imagepath);
      loadPixels();
      this.imagecolors.resize(this.imagecolors.width*compress_pct/100,0);
      this.cartesian_canvas=cartesian_canvas;
      this.imagecolors.loadPixels();
      this.imagedims = new int[] {(int)imagecolors.width, (int)imagecolors.height};
      //Map the points in the image to the model, once.
      for (LXPoint p : model.points) {
        int[] point_loc_in_img=scaleLocationInImageToLocationInBrain(p);
        this.pixel_to_pixel.put(p.index,point_loc_in_img);
      }
  }

  /**
  * Outputs one frame of the image in its' current state to the pixel mapping.
  * @param colors: The master colors array
  */
  public int[] ImageToPixels(int[] colors){
    int pixelcolor;
    float[] hsb_that_pixel;
    int[] loc_in_img;
    for (LXPoint p : model.points) {
      loc_in_img = scaleLocationInImageToLocationInBrain(p);
      pixelcolor = this.imagecolors.get(loc_in_img[0],loc_in_img[1]);
      colors[p.index]= lx.hsb(hue(pixelcolor),saturation(pixelcolor),brightness(pixelcolor));
    }
    return colors;
  }


  /**
  * Outputs one frame of the image in its' current state to the pixel mapping.
  * Current preferred method for using moving images. Faster than translating the image under the mapping.
  * @param colors: The master colors array
  */
  public int[] shiftedImageToPixels(float xpctshift,float ypctshift, int[] colors){
    int pixelcolor;
    float[] hsb_that_pixel;
    int[] loc_in_img;
    for (LXPoint p : model.points) {
      loc_in_img = scaleShiftedLocationInImageToLocationInBrain(p,xpctshift,ypctshift);
      pixelcolor = this.imagecolors.get(loc_in_img[0],loc_in_img[1]);
      colors[p.index]= lx.hsb(hue(pixelcolor),saturation(pixelcolor),brightness(pixelcolor));
    }
    return colors;
  }



  /**
  * Translates the image in either the x or y axis. 
  * Important to note that this is operating on the image itself, not on the pixel mapping, so it's just x and y
  * This seems to get worse performance than just recalculating the LED pixels across different positions in the image if looped.
  * Automatically wraps around.
  * @param which_axis: x or y or throw exception
  * @param pctrate: How much percentage of the image to translate?
  */
  public void translate_image(String which_axis, float pctrate) { //String which_axis, float percent, boolean wrap
    PImage translate_buffer;
    if (which_axis.equals("x")) {
      translate_buffer=imagecolors; 
      int rate = PApplet.parseInt(imagecolors.width*(pctrate/100.0f));
      for (int imgy = 0; imgy < imagecolors.height; imgy++) {
        for (int inc = 1; inc < rate+1; inc++) {
          imagecolors.set(imagecolors.width-inc,imgy,translate_buffer.get(0,imgy));
        }
      }
  
      for (int imgx = 0; imgx < imagecolors.width-rate; imgx++ ) {
        for (int imgy = 0; imgy < imagecolors.height; imgy++) {
          imagecolors.set(imgx,imgy,translate_buffer.get(imgx+rate,imgy));
        }
      }
    } else if (which_axis.equals("y")){
      translate_buffer=imagecolors; 
      int rate = PApplet.parseInt(imagecolors.height*(pctrate/100.0f));
      for (int imgx = 0; imgx < imagecolors.width; imgx++) {
        for (int inc = 1; inc < rate+1; inc++) {
          imagecolors.set(imgx,imagecolors.height-inc,translate_buffer.get(imgx,0));
        }
      }
  
      for (int imgy = 0; imgy < imagecolors.height-rate; imgy++ ) {
        for (int imgx = 0; imgx < imagecolors.width; imgx++) {
          imagecolors.set(imgx,imgy,translate_buffer.get(imgx,imgy+rate));
        }
      }
    } else{
      throw new IllegalArgumentException("Axis must be x or y. Image axis, not model axis :)");
    }
  }

  /**
  * Returns the coordinates for an LXPoint p (which has x,y,z) that correspond to a location on an image based on the coordinate system 
  * @param p: The LXPoint to get coordinates for.
  */
  private int[] scaleLocationInImageToLocationInBrain(LXPoint p) {
    float[][] minmaxxy;
    float newx;
    float newy;
    if (this.cartesian_canvas.equals("xy")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.yMin,model.yMax}};
      newx=(1-(p.x-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))*this.imagedims[0];
      newy=(1-(p.y-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("xz")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.zMin,model.zMax}};
      newx=(1-(p.x-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))*this.imagedims[0];
      newy=(1-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("yz")){
      minmaxxy=new float[][]{{model.yMin,model.yMax},{model.zMin,model.zMax}};
      newx=(1-(p.y-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))*this.imagedims[0];
      newy=(1-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_x")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.xMin,model.xMax}};
      newx=(1-((atan2(p.z,p.y)+PI)/(2*PI)))*this.imagedims[0];
      newy=(1-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_y")){
      minmaxxy=new float[][]{{model.yMin,model.yMax},{model.yMin,model.yMax}};
      newx=(1-((atan2(p.z,p.x)+PI)/(2*PI)))*this.imagedims[0];
      newy=(1-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_z")){
      minmaxxy=new float[][]{{model.zMin,model.zMax},{model.zMin,model.zMax}};
      newx=(1-((atan2(p.y,p.x)+PI)/(2*PI)))*this.imagedims[0];
      newy=(1-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))*this.imagedims[1];
    }
    else{
      throw new IllegalArgumentException("Must enter plane xy, xz, yz, or cylindrical_x/y/z");
    }
      int newxint=(int)newx;
      int newyint=(int)newy;
      if (newxint>=this.imagedims[0]){
         newxint=newxint-1;
      }
      if (newxint<=0){
         newxint=newxint+1;
      }
      if (newyint>=this.imagedims[1]){
         newyint=newyint-1;
      }
      if (newyint<=0){
         newyint=newyint+1;
      }
      int[] result = new int[] {newxint,newyint};
      return result;
  }





  /**
  * Returns the SHIFTED coordinates for an LXPoint p (which has x,y,z) that correspond to a location on an image based on the coordinate system 
  * This seems to get better performance in the run loop than using translate on the image repetitively.
  * @param p: The LXPoint to get coordinates for.
  * @param xpctshift: How far to move the image in the x direction, as a percent of the image width
  * @param ypctshift: How far to move the image in the y direction, as a percent of the image height
  */
  private int[] scaleShiftedLocationInImageToLocationInBrain(LXPoint p, float xpctshift, float ypctshift) {
    float[][] minmaxxy;
    float newx;
    float newy;
    if (this.cartesian_canvas.equals("xy")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.yMin,model.yMax}};
      newx=(1+xpctshift-(p.x-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.y-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("xz")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.zMin,model.zMax}};
      newx=(1+xpctshift-(p.x-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("yz")){
      minmaxxy=new float[][]{{model.yMin,model.yMax},{model.zMin,model.zMax}};
      newx=(1+xpctshift-(p.y-minmaxxy[0][0])/(minmaxxy[0][1]-minmaxxy[0][0]))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_x")){
      minmaxxy=new float[][]{{model.xMin,model.xMax},{model.xMin,model.xMax}};
      newx=(1+xpctshift-((atan2(p.z,p.y)+PI)/(2*PI)))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_y")){
      minmaxxy=new float[][]{{model.yMin,model.yMax},{model.yMin,model.yMax}};
      newx=(1+xpctshift-((atan2(p.z,p.x)+PI)/(2*PI)))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else if (this.cartesian_canvas.equals("cylindrical_z")){
      minmaxxy=new float[][]{{model.zMin,model.zMax},{model.zMin,model.zMax}};
      newx=(1+xpctshift-((atan2(p.y,p.x)+PI)/(2*PI)))%1.0f*this.imagedims[0];
      newy=(1+ypctshift-(p.z-minmaxxy[1][0])/(minmaxxy[1][1]-minmaxxy[1][0]))%1.0f*this.imagedims[1];
    }
    else{
      throw new IllegalArgumentException("Must enter plane xy, xz, yz, or cylindrical_x/y/z");
    }
      int newxint=(int)newx;
      int newyint=(int)newy;
      if (newxint>=this.imagedims[0]){
         newxint=newxint-1;
      }
      if (newxint<=0){
         newxint=newxint+1;
      }
      if (newyint>=this.imagedims[1]){
         newyint=newyint-1;
      }
      if (newyint<=0){
         newyint=newyint+1;
      }
      int[] result = new int[] {newxint,newyint};
      return result;
  }
}











class HueCyclePalette extends LXPalette {
  
  final BasicParameter zeriod = new BasicParameter("Period", 5000, 0, 30000);
  final BasicParameter spread = new BasicParameter("Spread", 2, 0, 8);
  final BasicParameter center = new BasicParameter("Center", model.cx - 10*INCHES, model.xMin, model.xMax);
  
  HueCyclePalette(LX lx) {
    super(lx);
    addParameter(zeriod);
    addParameter(spread);
    addParameter(center);
  
    zeriod.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        period.setValue(zeriod.getValue());
      }
    });
    
  }
  
  public double getHue(LXPoint p) {
    return super.getHue() + spread.getValue() * (abs(p.x - center.getValuef()) + abs(p.y - model.cy));
  }
}




/************** COLOR BREWER PALETTES *************/


public static class GradientCB {
  public static final int Sequential = 3;
  public static final int Diverging = 2;
  public static final int Qualitative = 1;
  
  public static Color[] getGradient(int paletteType, int paletteIndex, int colors) {
    boolean colorBlindSave = false;
    ColorBrewer[] palettes;
    if (paletteType == Sequential) { 
        palettes = ColorBrewer.getSequentialColorPalettes(colorBlindSave);
    } else if (paletteType == Diverging) { 
        palettes = ColorBrewer.getDivergingColorPalettes(colorBlindSave);
    } else { 
        palettes = ColorBrewer.getQualitativeColorPalettes(colorBlindSave);
    }
    Color[] gradient = palettes[paletteIndex].getColorPalette(colors);
    return gradient;
  }
}



public static class DBLPalette { 


  public static Color[] toColors(int[] ints) { 
    Color[] colors = new Color[ints.length];
    for (int i=0; i<ints.length; i++) {
      Color c = new Color(ints[i]);
      colors[i] = c;
    }
    return colors;
  }

  public static int[] toColorInts(Color[] colors) { 
    int[] ints = new int[colors.length];
    for (int i=0; i<colors.length; i++) {
      int c = colors[i].getRGB();
      ints[i] = c;
    }
    return ints;
  }

  public static Color[] interpolate(Color[] gradient, int colorCount) {
    Color[] colors = new Color[colorCount];
    float scale = (float)(gradient.length-1)/(float)(colorCount-1);

    for (int i = 0; i < colorCount; i++) {
      float value = scale * i;
      int index = (int)Math.floor(value);

      Color c1 = gradient[index];
      float remainder = 0.0f;
      Color c2 = null;
      if (index+1 < gradient.length) {
        c2 = gradient[index+1];
        remainder = value - index;
      } else {
        c2 = gradient[index];
      }
      //		 System.out.println("value: " + value + " index: " + index + " remainder: " + remainder);
      int red   = Math.round((1 - remainder) * c1.getRed()    + (remainder) * c2.getRed());
      int green = Math.round((1 - remainder) * c1.getGreen()  + (remainder) * c2.getGreen());
      int blue  = Math.round((1 - remainder) * c1.getBlue()   + (remainder) * c2.getBlue());

      colors[i] = new Color(red, green, blue);
    }
    return colors;
  }

  public static int[] interpolate(int[] gradient, int colorCount) { 
    Color[] gradientColor = toColors(gradient);
    int[] ints = toColorInts(interpolate(gradientColor, colorCount));   
    return ints;
  }

}
/**
 * This file has a bunch of example patterns, each illustrating the key
 * concepts and tools of the LX framework.
 */


/**
 * Basic Hello World pattern
*/
class HelloWorldPattern extends BrainPattern{ 

  private final BasicParameter colorChangeSpeed = new BasicParameter("SPD",  5000, 0, 10000);
  private final SinLFO whatcolor = new SinLFO(0, 360, colorChangeSpeed);
  
  public HelloWorldPattern(LX lx){
    super(lx);
    addParameter(colorChangeSpeed);
    addModulator(whatcolor).trigger();
  }

  public void run(double deltaMs){
    for (LXPoint p : model.points) {
      float h=whatcolor.getValuef();
      int s=100;
      int b=100;
      colors[p.index]=lx.hsb(h,s,b);
    }
  }
}




/** 
 * Demonstration of layering patterns
 */
class LayerDemoPattern extends LXPattern {
  
  private final BasicParameter colorSpread = new BasicParameter("Clr", 0.5f, 0, 3);
  private final BasicParameter stars = new BasicParameter("Stars", 100, 0, 100);
  
  public LayerDemoPattern(LX lx) {
    super(lx);
    addParameter(colorSpread);
    addParameter(stars);
    for (int i = 0; i < 200; ++i) {
      addLayer(new StarLayer(lx));
    }
    addLayer(new CircleLayer(lx));
    addLayer(new RodLayer(lx));
  }

  public void run(double deltaMs) {
    // The layers run automatically
  }

  private class CircleLayer extends LXLayer {

    private final SinLFO xPeriod = new SinLFO(3400, 7900, 11000); 
    private final SinLFO brightnessX = new SinLFO(model.xMin, model.xMax, xPeriod);

    private CircleLayer(LX lx) {
      super(lx);
      addModulator(xPeriod).start();
      addModulator(brightnessX).start();
    }

    public void run(double deltaMs) {
      // The layers run automatically
      float falloff = 100 / (4*FEET);
      for (LXPoint p : model.points) {
        float yWave = model.yRange/2 * sin(p.x / model.xRange * PI); 
        float distanceFromCenter = dist(p.x, p.y, model.cx, model.cy);
        float distanceFromBrightness = dist(p.x, abs(p.y - model.cy), brightnessX.getValuef(), yWave);
        colors[p.index] = LXColor.hsb(
          lx.getBaseHuef() + colorSpread.getValuef() * distanceFromCenter,
          100,
          max(0, 100 - falloff*distanceFromBrightness)
        );
      }
    }
  }

  private class RodLayer extends LXLayer {
    
    private final SinLFO zPeriod = new SinLFO(2000, 5000, 9000);
    private final SinLFO zPos = new SinLFO(model.zMin, model.zMax, zPeriod);
    
    private RodLayer(LX lx) {
      super(lx);
      addModulator(zPeriod).start();
      addModulator(zPos).start();
    }
    
    public void run(double deltaMs) {
      for (LXPoint p : model.points) {
        float b = 100 - dist(p.x, p.y, model.cx, model.cy) - abs(p.z - zPos.getValuef());
        if (b > 0) {
          addColor(p.index, LXColor.hsb(
            lx.getBaseHuef() + p.z,
            100,
            b
          ));
        }
      }
    }
  }
  
  private class StarLayer extends LXLayer {
    
    private final TriangleLFO maxBright = new TriangleLFO(0, stars, random(2000, 8000));
    private final SinLFO brightness = new SinLFO(-1, maxBright, random(3000, 9000)); 
    
    private int index = 0;
    
    private StarLayer(LX lx) { 
      super(lx);
      addModulator(maxBright).start();
      addModulator(brightness).start();
      pickStar();
    }
    
    private void pickStar() {
      index = (int) random(0, model.size-1);
    }
    
    public void run(double deltaMs) {
      if (brightness.getValuef() <= 0) {
        pickStar();
      } else {
        addColor(index, LXColor.hsb(lx.getBaseHuef(), 50, brightness.getValuef()));
      }
    }
  }
}





/**
 * Simplest demonstration of using the rotating master hue.
 * All pixels are full-on the same color.
 */
class TestHuePattern extends BrainPattern {
  
  public TestHuePattern(LX lx) {
    super(lx);
  }
  
  public void run(double deltaMs) {
    // Access the core master hue via this method call
    float hv = lx.getBaseHuef();
    for (int i = 0; i < colors.length; ++i) {
      colors[i] = lx.hsb(palette.getHuef(), 100, 100);
    }
  } 
}

class GradientPattern extends BrainPattern {
  GradientPattern(LX lx) {
    super(lx);
  }
  
  public void run(double deltaMs) {
    for (LXPoint p : model.points) {
      colors[p.index] = palette.getColor(p);
    }
  }
}




/**
 * Simple demonstration of using the MentalImage class
 * Chooses an image, and gradually rotates it across the brain.
 */
class TestImagePattern extends BrainPattern {

  MentalImage mentalimage = new MentalImage("media/images/starry_night.jpg","cylindrical_z",100);
  SortedMap<Integer, float[]> led_colors = new TreeMap<Integer, float[]>();
  int counter;
  float shift=0.0f;
  
  public TestImagePattern(LX lx) {
    super(lx);
  }
  
  public void run(double deltaMs) {
    shift+=0.0003f;
    if(shift>1){
      shift=0.0f;
    }
    colors=this.mentalimage.shiftedImageToPixels(shift,0, colors);
    float hv = lx.getBaseHuef();
  } 
}






/**
 * Test of a wave moving across the X axis.
 */
class TestXPattern extends BrainPattern {

  private final SinLFO xPos = new SinLFO(model.xMin, model.xMax, 4000);
  
  public TestXPattern(LX lx) {
    super(lx);
    addModulator(xPos).trigger();
  }
  
  public void run(double deltaMs) {
    float hv = lx.getBaseHuef();
    int i = 0;
    int j = 0;
    for (LXPoint p : model.points) {
      j +=1;
      // This is a common technique for modulating brightness.
      // You can use abs() to determine the distance between two
      // values. The further away this point is from an exact
      // point, the more we decrease its brightness
      float bv = max(0, 100 - abs(p.x - xPos.getValuef()));
      if (i < 10) {
        i += 1;
        System.out.println("index: " + p.index);
        println(j);
      }
      colors[p.index] = lx.hsb(hv, 100, bv);
    }
  }
}






/**
 * Test of hemispheres functionality
 */
class TestHemispheres extends BrainPattern {
  private final SinLFO xPos = new SinLFO(0, model.xMax, 4000);
  public TestHemispheres(LX lx) {
    super(lx);
    addModulator(xPos).trigger();
  }
  public void run(double deltaMs) {
    float hv = lx.getBaseHuef();
    int i = 0;
    int j = 0;
    Bar bar = model.barmap.get("FOG-LAW");
    Bar otherbar = model.barmap.get("LAW-OLD");;
    float x = bar.angle_with_bar(otherbar);
    //println(otherbar.id);
    //println(x);
    for (String bb : model.barmap.keySet()){
      Bar b = model.barmap.get(bb);
      hv=200;
      if (b.left_right_mid.equals("left")){
        hv=100;
      }
      if (b.left_right_mid.equals("right")){
        hv=200;
      }
      if (b.left_right_mid.equals("mid")){
        hv=300;
      }
      
      for (LXPoint p : b.points) {
        colors[p.index] = lx.hsb(hv, 100, 100);
      }
  }
}
}




  
/**
 * Test of lighting up the bars one by one rapidly. 
 * Todo: Make this way less ugly and more importantly, write one that traverses the node graph
 */
class TestBarPattern extends BrainPattern {
  public String current_bar_name="FOG-LAW"; //can be any 
  public String current_node_name="FOG";
  public Random randomness = new Random();
  public TestBarPattern(LX lx) {
    super(lx);
  }
  public void run(double deltaMs) {
    Random random = new Random();
    List<String> bar_node_names=Arrays.asList(current_bar_name.split("-"));
    String next_node_name = ""; 
    for (String node_name_i : bar_node_names){ 
      if (node_name_i.length()==3 && !node_name_i.equals(current_node_name)){ //is it a node name? is it not the same node name?
        next_node_name=node_name_i;
    }
    }
    Node next_node_node = model.nodemap.get(next_node_name);
    List<String> possible_next_bars = next_node_node.adjacent_bar_names;
    String next_bar_name = possible_next_bars.get(randomness.nextInt(possible_next_bars.size()));
    current_bar_name=next_bar_name;
    current_node_name=next_node_name;
    List<String> keys = new ArrayList<String>(model.barmap.keySet());
    String randomKey = keys.get( random.nextInt(keys.size()) );
    Bar b = model.barmap.get(next_bar_name);
    float hv = lx.getBaseHuef();
    int i = 0;
    int j = 0;
    for (LXPoint p: model.points) {
      colors[p.index]=lx.hsb(0,100,0);
    }
      for (LXPoint p: b.points) {
      j +=1;
      colors[p.index] = lx.hsb(100, 100, 100);
      }
    }
}

/**
 * Creates a really basic thundercloud with lightning strikes pattern
 * Also an example of basic node traversal
 */
class SuperBasicLightningStrikes extends BrainPattern {
  public String next_node_name;
  public List<Bar> lightning_bars = new ArrayList<Bar>();
  public String lightning_leading_node="ERA";
  public Bar b;
  public Random randomness = new Random();
  public Node next_node_node_name;
   int stage = 0; //0 = hasn't struck ground yet, 1-10 = has struck ground, 11+ = has struck ground and is expired

  public SuperBasicLightningStrikes(LX lx){
     super(lx);
  }
 
    
  public void run(double deltaMs) {
    for (LXPoint p: model.points) {
      if (p.z< 15){
        colors[p.index]=lx.hsb(random(200,260),70,random(0,50));
      }
      else {
        colors[p.index]=lx.hsb(random(200,260),20*(p.z/model.zMax),random(0,50));
      }
      
    }
    Node next_node_node_name = model.nodemap.get(lightning_leading_node); 
    if (!(next_node_node_name.ground) && lightning_bars.size()<15){
      List<String> possible_next_bars = next_node_node_name.adjacent_bar_names;
      float x= random(10);
      String next_bar = possible_next_bars.get(randomness.nextInt(possible_next_bars.size()));
      b = model.barmap.get(next_bar);
      lightning_bars.add(b);
      
      List<String> bar_node_names=Arrays.asList(next_bar.split("-"));
      for (String node_name_i : bar_node_names){ 
        if (node_name_i.length()==3 && !node_name_i.equals(lightning_leading_node)){ //is it a node name? is it not the same node name?
          next_node_name=node_name_i;
        }
      }
      lightning_leading_node=next_node_name;
      
      for (Bar lightning_bar_i : lightning_bars) {
        for (LXPoint p: lightning_bar_i.points) {
          colors[p.index]=lx.hsb(70,100,100);
        }
      }
    }
    else{
      
      lightning_bars = new ArrayList<Bar>();
      List<String> possible_nodes = new ArrayList<String>(model.nodemap.keySet());
      lightning_leading_node = possible_nodes.get(randomness.nextInt(possible_nodes.size()));
    }
  } 
}

/**
 * Selects random sets of bars and sets them to random colors fading in and out
 */
class RandomBarFades extends BrainPattern {
   
  SortedMap<String, Bar> active_bars = new TreeMap<String, Bar>();
  SortedMap<String, String> random_bar_colors = new TreeMap<String, String>();
  List<String> all_bar_names= new ArrayList<String>(model.barmap.keySet());;
  Random randomness = new Random();
  Bar b;
  String randomKey;
  int phase = -1;
  String random_color_str;
    
  public RandomBarFades(LX lx){
    super(lx);
  }


  public void run(double deltaMs) {
    if (phase < 0){  
      for (int i = 0; i < 400; i=i+1) {
        String stringi = str(i);
        randomKey = all_bar_names.get( randomness.nextInt(all_bar_names.size()) );
        b = model.barmap.get(randomKey);
        active_bars.put(stringi,b);
        random_color_str = str(PApplet.parseInt(random(360)));
        random_bar_colors.put(stringi,random_color_str);
        phase=1;
      }
    }
    phase=phase+3;
    if (phase < 100){
      for (String j : active_bars.keySet()){
        Bar bb = active_bars.get(j);
        random_color_str = random_bar_colors.get(j);
        for (LXPoint p : bb.points) {
          colors[p.index]=lx.hsb(PApplet.parseInt(random_color_str),100,phase);
        }
      }
    }
    else{
      for (String j : active_bars.keySet()){
        Bar bb = active_bars.get(j);
        random_color_str = random_bar_colors.get(j);
        for (LXPoint p : bb.points) {
          colors[p.index]=lx.hsb(PApplet.parseInt(random_color_str),100,200-phase);
        }
      }
    }
    if (phase>200){
      phase=phase % 200;
      for (LXPoint p: model.points) {
        colors[p.index]=lx.hsb(0,0,0);
      }
      active_bars = new TreeMap<String, Bar>();
      random_bar_colors = new TreeMap<String, String>();
      for (int i = 0; i < 400; i++) {
        String stringi = str(i);
        String randomKey = all_bar_names.get( randomness.nextInt(all_bar_names.size()) );
        b = model.barmap.get(randomKey);
        active_bars.put(stringi,b);
        random_color_str = str(PApplet.parseInt(random(360)));
        random_bar_colors.put(stringi,random_color_str);
      }
   }  
  }
}



 
 
 

class RainbowBarrelRoll extends BrainPattern {
   float hoo;
   float anglemod = 0;
    
  public RainbowBarrelRoll(LX lx){
     super(lx);
  }
  
 public void run(double deltaMs) {
     anglemod=anglemod+1;
     if (anglemod > 360){
       anglemod = anglemod % 360;
     }
     
    for (LXPoint p: model.points) {
      //conveniently, hue is on a scale of 0-360
      hoo=((atan(p.x/p.z))*360/PI+anglemod);
      colors[p.index]=lx.hsb(hoo,80,100);
    }
  }
}


class SampleNodeTraversal extends BrainPattern{
  Node randomnode;
  Node nextrandomnode;
  List<Bar> barlist;
  
  
  public SampleNodeTraversal(LX lx){
    super(lx);
    randomnode = model.getRandomNode();
  }

  public void run(double deltaMS) {
    randomnode = randomnode.random_adjacent_nodes(1).get(0);
    nextrandomnode = randomnode.random_adjacent_nodes(1).get(0);
    barlist = randomnode.adjacent_bars();
    List<LXPoint> bar_points = nodeToNodePoints(randomnode,nextrandomnode);
    for (LXPoint p: model.points) {
      colors[p.index]=lx.hsb(30,55,100);
    }

    for (Bar b: barlist) {
      for (LXPoint p: b.points){
        colors[p.index]=lx.hsb(200,256,100);
      }
    }

    int counta=0;
    for (LXPoint p:bar_points){
      counta+=10;
      colors[p.index]=lx.hsb(counta,counta/2,100);
    }
  }
}

class SampleNodeTraversalWithFade extends BrainPattern{
  Node randnod = model.getRandomNode();
  Node randnod2 = model.getRandomNode();
  private final BasicParameter colorFade = new BasicParameter("Fade", 0.95f, 0.9f, 1.0f);
  List<Bar> barlist;

  public SampleNodeTraversalWithFade(LX lx){
    super(lx);
    addParameter(colorFade);
    for (LXPoint p: model.points) {
      colors[p.index]=lx.hsb(0,0,0);
    }
  }

  public void run(double deltaMS) {
    randnod = randnod.random_adjacent_nodes(1).get(0);
    randnod2 = randnod.random_adjacent_nodes(1).get(0);
    barlist = randnod.adjacent_bars();
    List<LXPoint> bar_poince = nodeToNodePoints(randnod,randnod2);
    for (LXPoint p: model.points) {
      colors[p.index] = LXColor.scaleBrightness(colors[p.index], colorFade.getValuef());
    }

    for (Bar b: barlist) {
      for (LXPoint p: b.points){
        colors[p.index]=lx.hsb(200,100,100);
      }
    }
    int counta=0;
    for (LXPoint p:bar_poince){
      counta+=10;
      colors[p.index]=lx.hsb(counta,counta/2,100);
    }
  }
}

 
class CircleBounce extends LXPattern {
  
  private final BasicParameter bounceSpeed = new BasicParameter("BNC",  1000, 0, 10000);
  private final BasicParameter colorSpread = new BasicParameter("CLR", 0.5f, 0.0f, 3.0f);
  private final BasicParameter colorFade   = new BasicParameter("FADE", 1, 0.0f, 10.0f);

  public CircleBounce(LX lx) {
    super(lx);
    addParameter(bounceSpeed);
    addParameter(colorSpread);
    addParameter(colorFade);
    addLayer(new CircleLayer(lx));
  }

  public void run(double deltaMs) {
    // The layers run automatically
  }

  private class CircleLayer extends LXLayer {
    private final SinLFO xPeriod = new SinLFO(model.zMin, model.zMax, bounceSpeed); 
    //private final SinLFO brightnessX = new SinLFO(model.xMin, model.xMax, xPeriod);

    private CircleLayer(LX lx) {
      super(lx);
      addModulator(xPeriod).start();
      //addModulator(brightnessX).start();
    }

    public void run(double deltaMs) {
      // The layers run automatically
      float falloff = 5.0f / colorFade.getValuef();
      //println("Height: ", xPeriod.getValuef());
      for (LXPoint p : model.points) {
        //float yWave = model.yRange/2 * sin(p.x / model.xRange * PI); 
        //float distanceFromCenter = dist(p.x, p.y, model.cx, model.cy);
        float distanceFromBrightness = abs(xPeriod.getValuef() - p.z);
        colors[p.index] = LXColor.hsb(
          lx.getBaseHuef() + colorSpread.getValuef(),
          100.0f,
          max(0.0f, 100.0f - falloff*distanceFromBrightness)
        );
      }
    }
  }
}

class CirclesBounce extends LXPattern {
  
  private final BasicParameter bounceSpeed = new BasicParameter("BNC",  1000, 0, 10000);
  private final BasicParameter colorSpread = new BasicParameter("CLR", 0.5f, 0.0f, 360.0f);
  private final BasicParameter colorFade   = new BasicParameter("FADE", 1, 0.0f, 10.0f);
  private SinLFO colorPeriod = new SinLFO(0, 1000, bounceSpeed);
  //private Color[] gradient = GradientCB.getGradient(3, 1, 20);
  private int[] baseGradient = 
      Colour.colorSchemeOfType(
          Colour.Colours.successColor(), 
          Colour.ColorScheme.ColorSchemeMonochromatic
      );

  private int[] gradient = DBLPalette.interpolate(baseGradient, 100);

  public CirclesBounce(LX lx) {
    super(lx);
    addParameter(bounceSpeed);
    addParameter(colorSpread);
    addParameter(colorFade);
    addLayer(new CirclesLayer(lx, 0));
    addLayer(new CirclesLayer(lx, 1));
    addLayer(new CirclesLayer(lx, 2));
    //println(gradient);
  }

  public void run(double deltaMs) {
    // The layers run automatically
  }

  private class CirclesLayer extends LXLayer {
    private SinLFO xPeriod = new SinLFO(model.xMin, model.xMax, bounceSpeed);
    private SinLFO yPeriod = new SinLFO(model.yMin, model.yMax, bounceSpeed);
    private SinLFO zPeriod = new SinLFO(model.zMin, model.zMax, bounceSpeed);
    private int xyz;
    //private final SinLFO brightnessX = new SinLFO(model.xMin, model.xMax, xPeriod);

    private CirclesLayer(LX lx, int _xyz) {
      super(lx);
      xyz = _xyz;
      addModulator(colorPeriod).start();
      addModulator(xPeriod).start();
      addModulator(yPeriod).start();
      addModulator(zPeriod).start();
      //addModulator(brightnessX).start();
    }

    public void run(double deltaMs) {
      // The layers run automatically
      float falloff = 5.0f / colorFade.getValuef();
      //println("Height: ", xPeriod.getValuef());
      for (LXPoint p : model.points) {
        //float yWave = model.yRange/2 * sin(p.x / model.xRange * PI); 
        //float distanceFromCenter = dist(p.x, p.y, model.cx, model.cy);
        float distanceFromBrightness = 0.0f;
        if (xyz==0) { distanceFromBrightness = abs(xPeriod.getValuef() - p.x); }
        if (xyz==1) { distanceFromBrightness = abs(yPeriod.getValuef() - p.y); }
        if (xyz==2) { distanceFromBrightness = abs(zPeriod.getValuef() - p.z); }
        /*
        colors[p.index] = LXColor.hsb(
          lx.getBaseHuef() + colorSpread.getValuef(),
          100.0,
          max(0.0, 100.0 - falloff*distanceFromBrightness)
        );
        int increment = (int)(deltaMs / (colorSpread.getValuef()+1.0));
        println("Indrement: ", increment);
        */
        //Color clr = gradient[(p.index+(int)colorPeriod.getValue()) % gradient.length];
        //colors[p.index] = clr.getRGB();
        /*
        int rgb = clr.getRGB();
        colors[p.index] = LXColor.hsb(
          hue(rgb),
          saturation(rgb),
          brightness(rgb) * max(0.0, 100.0 - falloff*distanceFromBrightness) * 0.01
        );
        */
        colors[p.index] = gradient[p.index % gradient.length];
        //colors[p.index] = gradient[(p.index+(int)colorPeriod.getValue()) % gradient.length];
      }
    }
  }
}




 /*
class SnakesDemoPattern extends BrainPattern {
  
}
*/

//Owner: Maki, though feel free to modify (doesn't work yet)
//Pattern: Neurons are represented by nodes, which decay but build up charge in response to sound
//At a certain amount of charge, it discharges into the surrounding bars which gets captured by other neurons
/*
class NeuronsFiring extends LXPattern {



  class Neuron {
    public Node centerNode;
    public float charge;
    public float something; 
    public enum state { NEURON_NOT_FIRING, NEURON_FIRING }

    public Neuron(Node centerNode){
      this.centerNode = centerNode;
      this.charge=10;

    }
  }

  public NeuronsFiring(LX lx) {
    super(lx);
    addLayer(new NeuronLayer(lx));
  }

  public void run(double deltaMs) {
    //layers run automatically
  }

  private class NeuronLayer extends LXLayer {

    private final BasicParameter numNeurons = new BasicParameter("Neurons",1,0,20);

    private NeuronLayer(LX lx) {
      super(lx);
      addParameter(numNeurons);
    }

    public void run(double deltaMS) {
      for (p : model.points) {
        if 
      }
    }
  }

}
*/

/*
class SampleImageScroll extends BrainPattern{
  Node randnod = model.getRandomNode();
  Node randnod2 = model.getRandomNode();
  List<Bar> barlist;
  
  
  public SampleNodeTraversal(LX lx){
    super(lx);
  }

  public void run(double deltaMS) {
    randnod = randnod.random_adjacent_nodes(1).get(0);
    randnod2 = randnod.random_adjacent_nodes(1).get(0);
    barlist = randnod.adjacent_bars();
    List<LXPoint> bar_poince = nodeToNodePoints(randnod,randnod2);
    for (LXPoint p: model.points) {
      colors[p.index]=lx.hsb(30,55,100);
    }

    for (Bar b: barlist) {
      for (LXPoint p: b.points){
        colors[p.index]=lx.hsb(200,256,100);
      }
    }

    int counta=0;
    for (LXPoint p:bar_poince){
      counta+=10;
      colors[p.index]=lx.hsb(counta,counta/2,100);
    }
  }
}

*/

class EQTesting extends BrainPattern {
  private GraphicEQ eq = null;
  List<List<LXPoint>> strips_emanating_from_nodes = new ArrayList<List<LXPoint>>();

  private DecibelMeter dbMeter = new DecibelMeter(lx.audioInput());

  /*
  public class BassWorm(){
    
    Node start_node;
    ArrayList<Node> wormnodes = new ArrayList<Node>;
    ArrayList<LXPoint> wormpoints = new ArrayList<LXPoint>();
      
    BassWorm(int numBars) {
      start_node = model.getRandomNode();
      next_node = start_node;
      previous_node = start_node;
      wormnodes.add(start_node);
      for (int i, int <= numBars, i++){
        while(wormnodes.contains(next_node){
          next_node = previous_node.random_adjacent_node();
        }
        wormnodes.add(next_node);
        new_points = nodeToNodePoints(previous_node,next_node);
        for (LXPoint p : new_points){
          wormpoints.add(p);
        }
        previous_node = next_node;
      }
    }
  }*/
  
  public EQTesting(LX lx) {
    super(lx);
    /*if (eq == null) {
      eq = new GraphicEQ(lx.audioInput());
      eq.range.setValue(48);
      eq.release.setValue(800);
      eq.gain.setValue(-6);
      eq.slope.setValue(6);
      addParameter(eq.gain);
      addParameter(eq.range);
      addParameter(eq.attack);
      addParameter(eq.release);
      addParameter(eq.slope);
      addModulator(eq).start();
    }*/
      addModulator(dbMeter).start();
      for (String n : model.nodemap.keySet()) {
        List<LXPoint> out_from_node = new ArrayList<LXPoint>();
        Node node = model.nodemap.get(n);
        List<Node> neighbornodes = node.adjacent_nodes();
        for (Node nn : neighbornodes) {
          out_from_node = nodeToNodePoints(node,nn);
          strips_emanating_from_nodes.add(out_from_node);
        }
      }
  }
  
  public void run(double deltaMs) {
    
    //float bassLevel = lx.audioInput.mix.level();//eq.getAveragef(0, 5) * 5000;
    float soundLevel = -dbMeter.getDecibelsf()*0.5f;
    //println(bassLevel);
    for (LXPoint p: model.points) {
      colors[p.index] = lx.hsb(random(100,120),40,40);
    }
    for (List<LXPoint> strip : strips_emanating_from_nodes) {
      int distance_from_node=0;
      int striplength = strip.size();
      for (LXPoint p : strip) {
        distance_from_node+=1;
        float relative_distance = (float) distance_from_node / striplength;
        float hoo = 300- 5*relative_distance*2500/soundLevel;
        float saturat = 100;
        float britness = max(0, 100 - 3*relative_distance*2500/soundLevel);
        addColor(p.index, lx.hsb(hoo, saturat, britness));
      }
    }
  }
}
/**
 * Here's a simple extension of a camera component. This will be
 * rendered inside the camera view context. We just override the
 * onDraw method and invoke Processing drawing methods directly.
 */






class UIBrainComponent extends UI3dComponent {
 
  final UIPointCloudVBO pointCloud = new UIPointCloudVBO();
  
  public void onDraw(UI ui, PGraphics pg) {
    int[] simulationColors = lx.getColors();
    simulationColors = lx.engine.getChannel(0).getColors();
    long simulationStart = System.nanoTime();
   // translate(0,50,-400); //remove this if we're using whole brain
    rotateX(PI*4.1f);
    drawSimulation(simulationColors);
    camera(); 
    strokeWeight(1);
  }
  
  public void drawSimulation(int[] simulationColors) {

    noStroke();
    noFill();
    pointCloud.draw(simulationColors);
  }
}




 
class UIWalls extends UI3dComponent {
  
  private final float WALL_MARGIN = 2*FEET;
  private final float WALL_SIZE = model.xRange + 2*WALL_MARGIN;
  private final float WALL_THICKNESS = 1*INCHES;
  
  protected void onDraw(UI ui, PGraphics pg) {
    fill(0xff666666);
    noStroke();
    pushMatrix();
    translate(model.cx, model.cy, model.zMax + WALL_MARGIN);
    box(WALL_SIZE, WALL_SIZE, WALL_THICKNESS);
    translate(-model.xRange/2 - WALL_MARGIN, 0, -model.zRange/2 - WALL_MARGIN);
    box(WALL_THICKNESS, WALL_SIZE, WALL_SIZE);
    translate(model.xRange + 2*WALL_MARGIN, 0, 0);
    box(WALL_THICKNESS, WALL_SIZE, WALL_SIZE);
    translate(-model.xRange/2 - WALL_MARGIN, model.yRange/2 + WALL_MARGIN, 0);
    box(WALL_SIZE, WALL_THICKNESS, WALL_SIZE);
    translate(0, -model.yRange - 2*WALL_MARGIN, 0);
    box(WALL_SIZE, WALL_THICKNESS, WALL_SIZE);
    popMatrix();
  }
}

class UIEngineControl extends UIWindow {
  
  final UIKnob fpsKnob;
  
  UIEngineControl(UI ui, float x, float y) {
    super(ui, "ENGINE", x, y, UIChannelControl.WIDTH, 96);
        
    y = UIWindow.TITLE_LABEL_HEIGHT;
    new UIButton(4, y, width-8, 20) {
      protected void onToggle(boolean enabled) {
        lx.engine.setThreaded(enabled);
        fpsKnob.setEnabled(enabled);
      }
    }
    .setActiveLabel("Multi-Threaded")
    .setInactiveLabel("Single-Threaded")
    .addToContainer(this);
    
    y += 24;
    fpsKnob = new UIKnob(4, y);    
    fpsKnob
    .setParameter(lx.engine.framesPerSecond)
    .setEnabled(lx.engine.isThreaded())
    .addToContainer(this);
  }
}

class UIComponentsDemo extends UIWindow {
  
  static final int NUM_KNOBS = 4; 
  final BasicParameter[] knobParameters = new BasicParameter[NUM_KNOBS];  
  
  UIComponentsDemo(UI ui, float x, float y) {
    super(ui, "UI COMPONENTS", x, y, 140, 10);
    
    for (int i = 0; i < knobParameters.length; ++i) {
      knobParameters[i] = new BasicParameter("Knb" + (i+1), i+1, 0, 4);
      knobParameters[i].addListener(new LXParameterListener() {
        public void onParameterChanged(LXParameter p) {
          println(p.getLabel() + " value:" + p.getValue());
        }
      });
    }
    
    y = UIWindow.TITLE_LABEL_HEIGHT;
    
    new UIButton(4, y, width-8, 20)
    .setLabel("Toggle Button")
    .addToContainer(this);
    y += 24;
    
    new UIButton(4, y, width-8, 20)
    .setActiveLabel("Boop!")
    .setInactiveLabel("Momentary Button")
    .setMomentary(true)
    .addToContainer(this);
    y += 24;
    
    for (int i = 0; i < 4; ++i) {
      new UIKnob(4 + i*34, y)
      .setParameter(knobParameters[i])
      .setEnabled(i % 2 == 0)
      .addToContainer(this);
    }
    y += 48;
    
    for (int i = 0; i < 4; ++i) {
      new UISlider(UISlider.Direction.VERTICAL, 4 + i*34, y, 30, 60)
      .setParameter(new BasicParameter("VSl" + i, (i+1)*.25f))
      .setEnabled(i % 2 == 1)
      .addToContainer(this);
    }
    y += 64;
    
    for (int i = 0; i < 2; ++i) {
      new UISlider(4, y, width-8, 24)
      .setParameter(new BasicParameter("HSl" + i, (i + 1) * .25f))
      .setEnabled(i % 2 == 0)
      .addToContainer(this);
      y += 28;
    }
    
    new UIToggleSet(4, y, width-8, 24)
    .setParameter(new DiscreteParameter("Ltrs", new String[] { "A", "B", "C", "D" }))
    .addToContainer(this);
    y += 28;
    
    for (int i = 0; i < 4; ++i) {
      new UIIntegerBox(4 + i*34, y, 30, 22)
      .setParameter(new DiscreteParameter("Dcrt", 10))
      .addToContainer(this);
    }
    y += 26;
    
    new UILabel(4, y, width-8, 24)
    .setLabel("This is just a label.")
    .setAlignment(CENTER, CENTER)
    .setBorderColor(ui.theme.getControlDisabledColor())
    .addToContainer(this);
    y += 28;
    
    setSize(width, y);
  }
} 


class UIPointCloudVBO {

  PShader shader;
  FloatBuffer vertexData;
  int vertexBufferObjectName;
  
  UIPointCloudVBO() {
    // Load shader
    shader = loadShader("frag.glsl", "vert.glsl");
    // Create a buffer for vertex data
    vertexData = ByteBuffer
      .allocateDirect(model.points.size() * 7 * Float.SIZE/8)
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer();
    
    // Put all the points into the buffer
    vertexData.rewind();
    for (LXPoint point : model.points) {
      // Each point has 7 floats, XYZRGBA
      vertexData.put(point.x);
      vertexData.put(point.y);
      vertexData.put(point.z);
      vertexData.put(0f);
      vertexData.put(0f);
      vertexData.put(0f);
      vertexData.put(1f);
    }
    vertexData.position(0);
    
    // Generate a buffer binding
    IntBuffer resultBuffer = ByteBuffer
      .allocateDirect(1 * Integer.SIZE/8)
      .order(ByteOrder.nativeOrder())
      .asIntBuffer();
    
    PGL pgl = beginPGL();
    pgl.genBuffers(1, resultBuffer); // Generates a buffer, places its id in resultBuffer[0]
    vertexBufferObjectName = resultBuffer.get(0); // Grab our buffer name
    endPGL();
  }
  
  public void draw(int[] colors) {
    // Put our new colors in the vertex data
    for (int i = 0; i < colors.length; ++i) {
      int c = colors[i];

      vertexData.put(7*i + 3, (0xff & (c >> 16)) / 255f); // R
      vertexData.put(7*i + 4, (0xff & (c >> 8)) / 255f); // G
      vertexData.put(7*i + 5, (0xff & (c)) / 255f); // B
    }
    
    PGL pgl = beginPGL();
    
    // Bind to our vertex buffer object, place the new color data
    pgl.bindBuffer(PGL.ARRAY_BUFFER, vertexBufferObjectName);
    pgl.bufferData(PGL.ARRAY_BUFFER, colors.length * 7 * Float.SIZE/8, vertexData, PGL.DYNAMIC_DRAW);
    
    shader.bind();
    int vertexLocation = pgl.getAttribLocation(shader.glProgram, "vertex");
    int colorLocation = pgl.getAttribLocation(shader.glProgram, "color");
    pgl.enableVertexAttribArray(vertexLocation);
    pgl.enableVertexAttribArray(colorLocation);
    pgl.vertexAttribPointer(vertexLocation, 3, PGL.FLOAT, false, 7 * Float.SIZE/8, 0);
    pgl.vertexAttribPointer(colorLocation, 4, PGL.FLOAT, false, 7 * Float.SIZE/8, 3 * Float.SIZE/8);
    javax.media.opengl.GL2 gl2 = (javax.media.opengl.GL2) ((PJOGL)pgl).gl;
    gl2.glPointSize(2);
    pgl.drawArrays(PGL.POINTS, 0, colors.length);
    pgl.disableVertexAttribArray(vertexLocation);
    pgl.disableVertexAttribArray(colorLocation);
    shader.unbind();
    
    pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    endPGL();
  }
}



class UIGlobalControl extends UIWindow {
  UIGlobalControl(UI ui, float x, float y) {
    super(ui, "GLOBAL", x, y, 140, 246);
    float yp = TITLE_LABEL_HEIGHT;
    final UIColorSwatch swatch = new UIColorSwatch(palette, 4, yp, width-8, 60) {
      protected void onDraw(UI ui, PGraphics pg) {
        super.onDraw(ui, pg);
        if (palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE) {
          palette.clr.hue.setValue(palette.getHue());
          redraw();
        }
      }
    };
    new UIKnob(4, yp).setParameter(palette.spread).addToContainer(this);
    new UIKnob(40, yp).setParameter(palette.center).addToContainer(this);
    
    final BooleanParameter hueCycle = new BooleanParameter("Cycle", palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE);
    new UISwitch(76, yp).setParameter(hueCycle).addToContainer(this);
    yp += 48;
    
    swatch.setEnabled(palette.hueMode.getValuei() == LXPalette.HUE_MODE_STATIC).setPosition(4, yp).addToContainer(this);
    yp += 64;
    
    hueCycle.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        palette.hueMode.setValue(hueCycle.isOn() ? LXPalette.HUE_MODE_CYCLE : LXPalette.HUE_MODE_STATIC);
      }
    });
    
    palette.hueMode.addListener(new LXParameterListener() {
      public void onParameterChanged(LXParameter p) {
        swatch.setEnabled(palette.hueMode.getValuei() == LXPalette.HUE_MODE_STATIC);
        hueCycle.setValue(palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE);
      }
    });
    
    new UISlider(3, yp, width-6, 30).setParameter(palette.zeriod).setLabel("Color Speed").addToContainer(this);
    yp += 58;
    new UISlider(3, yp, width-6, 30).setParameter(lx.engine.speed).setLabel("Speed").addToContainer(this);
  }
}

/**
* Hackathon patterns go here!
* @author: You. 
*/
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DBL2_lighting" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
