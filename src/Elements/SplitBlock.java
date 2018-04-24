package Elements;

import Logic.Logic;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SplitBlock extends Block {

    public SplitBlock(Logic parent) {
        this.parent = parent;
        this.name = "-<";
        this.maxInPorts = 1;
        this.maxOutPorts = 2;
        this.valDefined = false;
        this.value = 0.0;
        this.id = (int) Math.random();

        System.out.println("Split block " + this.id + " created.");
    }

    @Override
    public void execute() {
        // Value reset
        if (!this.valDefined) {
            this.value = 0.0;
        }

        if (this.maxInPorts != this.inputPorts.size()) { return;}
        try {
            this.value = this.inputPorts.get(0).getValue();
            this.valDefined = true;
        } catch (IOException e) {
            this.value = 0.0;
            this.valDefined = false;
        }
    }
}