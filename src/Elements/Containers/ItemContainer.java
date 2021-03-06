package Elements.Containers;

import Elements.Blocks.Block;
import Elements.Ports.Connection;
import Elements.Ports.InputPort;
import Elements.Ports.OutputPort;
import Elements.Ports.Port;
import Logic.Logic;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author xgrigo02
 */
public class ItemContainer implements Serializable {

    private ArrayList <BlockSave> blocks;
    private ArrayList <ConnectionSave> connections;
    private ArrayList <PortSave> ports;
    private int id;
    private String name;

    /**
     * Constructor for ItemContainer.
     */
    public ItemContainer() {
        blocks = new ArrayList<>();
        connections = new ArrayList<>();
        ports = new ArrayList<>();
        name = null;
        id = 0;
    }

    /**
     * New ID generator for all scheme elements.
     * @return int new ID
     */
    public int generateId() {
        return id++;
    }

    /**
     * Getter for container block saves.
     *
     * @return ArrayList BlockSaves
     */
    public ArrayList<BlockSave> getBlocks() {
        return blocks;
    }

    /**
     * Getter for container port saves.
     *
     * @return ArrayList PortSaves
     */
    public ArrayList<PortSave> getPorts() {
        return ports;
    }

    /**
     * Getter for container connection saves.
     *
     * @return ArrayList ConnectionSaves
     */
    public ArrayList<ConnectionSave> getConnections() {
        return connections;
    }

    /**
     * Adds block save to container
     * @param save BlockSave to add
     */
    public void addBlock(BlockSave save) {
        if (this.blocks.contains(save)) {
            this.blocks.set(this.blocks.indexOf(save), save);
        }
        else {
            this.blocks.add(save);
        }
    }

    /**
     * Adds connection save to container
     * @param save ConnectionSave to add
     */
    public void addConnection(ConnectionSave save) {
        if (this.connections.contains(save)) {
            this.connections.set(this.connections.indexOf(save), save);
        }
        else {
            this.connections.add(save);
        }
    }

    /**
     * Adds port save to container
     * @param save PortSave to add
     */
    public void addPort(PortSave save) {
        if (this.ports.contains(save)) {
            this.ports.set(this.ports.indexOf(save), save);
        }
        else {
            this.ports.add(save);
        }
    }

    /**
     * Removes save form ItemContainer
     * @param o object to delete
     */
    public void remove(Object o) {
        ArrayList<BlockSave> blocksToRemove = new ArrayList<>();
        ArrayList<PortSave> portsToRemove = new ArrayList<>();
        ArrayList<ConnectionSave> consToRemove = new ArrayList<>();
        if (o instanceof Block) {
            for (BlockSave block : this.blocks) {
                if (block.getId() == ((Block) o).getId()) {
                    blocksToRemove.add(block);
                }
            }

            for (PortSave port : this.ports) {
                if (port.getBlockId() == ((Block) o).getId()) {
                    portsToRemove.add(port);
                    for (ConnectionSave con : this.connections) {
                        if (con.getToPortId() == port.getId()
                                || con.getFromPortId() == port.getId()) {
                            consToRemove.add(con);
                        }
                    }
                }
            }
        }
        else if (o instanceof Port) {
            for (PortSave port : this.ports) {
                if (port.getId() == ((Port) o).getId()) {
                    portsToRemove.add(port);
                    for (ConnectionSave con: this.connections) {
                        if (port.getId() == con.getFromPortId()
                                || port.getId() == con.getToPortId()) {
                            consToRemove.add(con);
                        }
                    }
                }
            }
        }
        else if (o instanceof Connection) {
            for (ConnectionSave con : this.connections) {
                if (con.getId() == ((Port) o).getId()) {
                    consToRemove.add(con);
                }
            }
        }

        this.connections.removeAll(consToRemove);
        this.ports.removeAll(portsToRemove);
        this.blocks.removeAll(blocksToRemove);
    }

    /**
     * Restores scheme from ItemContainer
     * @param logic     Logic to be modified
     * @param scheme    Scheme to update
     */
    public void restore(Logic logic, AnchorPane scheme) {
        Hashtable<Integer, Block> rest_blocks = new Hashtable<>();
        Hashtable<Integer, Port> rest_ports = new Hashtable<>();
        Hashtable<Integer, Connection> rest_cons = new Hashtable<>();
        Block tmp_bl;
        Port tmp_prt;
        Connection tmp_con;

        for (BlockSave bl : this.blocks) {
            rest_blocks.put(bl.getId(), bl.restore(logic, scheme));
        }
        for (PortSave prt : ports) {
            Block tmp = rest_blocks.get(prt.getBlockId());
            rest_ports.put(prt.getId(), prt.restore(logic, tmp));
        }
        for (ConnectionSave con : connections) {
            rest_cons.put(con.getId(), con.restore(logic, scheme));
        }

        try {
            for (ConnectionSave conS : connections) {
                tmp_con = rest_cons.get(conS.getId());
                tmp_prt = rest_ports.get(conS.getFromPortId());
                tmp_prt.setConnection(tmp_con);

                tmp_prt = rest_ports.get(conS.getToPortId());
                tmp_prt.setConnection(tmp_con);
            }
        } catch (IOException e) {
            System.err.println("Can't restore scheme from save");
            e.printStackTrace();
            Platform.exit();
            System.exit(99);
        }

        for (BlockSave blS : blocks) {
            tmp_bl = rest_blocks.get(blS.getId());
            for (Integer portId : blS.getInPorts()) {
                tmp_bl.addInPort((InputPort) rest_ports.get(portId));
            }
            for (Integer portId : blS.getOutPorts()) {
                tmp_bl.addOutPort((OutputPort) rest_ports.get(portId));
            }
        }
        logic.getBlocks().addAll(rest_blocks.values());
    }

    /**
     * Getter for scheme save name
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for scheme save name
     * @param name name of scheme save
     */
    public void setName(String name) {
        this.name = name;
    }
}
