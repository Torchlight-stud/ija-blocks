package Elements.Ports;

import Elements.Blocks.Block;
import Elements.Containers.ItemContainer;
import Elements.DataTypes.DataType;
import Interface.SingConElm;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import Logic.Logic;
import java.io.IOException;
import java.io.Serializable;

public abstract class Port implements SingConElm, Serializable {

    // TODO: rewrite to "dataType"
    protected Connection conTo;
    protected Block parent;
    protected Pane stack;
    protected Logic logic;
    protected int id;
    protected boolean blocked;
    protected boolean selected;

    private int sizeX = 10;
    private int sizeY = 10;
    protected double layoutX;
    protected double layoutY;
    private Rectangle shape;

    private Color stColor = Color.GRAY;
    private Color actColor = Color.CYAN.darker();
    private Color selColor = Color.RED;

    public boolean isConnected() {
	    return this.conTo != null;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void makeSelected(boolean bool) {
        double strokeWidth = this.shape.getStrokeWidth();
        if (bool) {
            if (!isSelected()) {
                this.shape.setStroke(selColor);
                this.selected = true;
            }
        }
        else{
            if (isSelected()) {
                this.shape.setStroke(stColor);
                this.selected = false;
            }
        }
        this.shape.setStrokeWidth(strokeWidth);
    }

	abstract public void setConnection(Connection con) throws IOException;

    public void removeConnection() {
        this.conTo = null;
    }

    public boolean isActive() {
        if (this.conTo == null) { return false; }
        else if (this.blocked) { return false; }
        Block from = this.conTo.getFrom().getParent();
        return from.isActive();
    }

    public DataType getData() {
        assert (this instanceof InputPort) : "Can getData only from Output ports";
        if (this.conTo == null) { return null; }
        else if (this.blocked) { return null; }
        Block from = this.conTo.getFrom().getParent();
        return from.getData();
    }

    public void dataAccepted() {
        assert (this instanceof InputPort) : "Can accept data only from Output port";
        this.conTo.getFrom().getParent().dataAccepted(this);
    }

    public void setActive() {
        if (this instanceof OutputPort &&  getConTo() != null) {
            this.conTo.setActive();
        }
        double strokeWidth = this.shape.getStrokeWidth();
        this.shape.setStroke(actColor);
        this.shape.setStrokeWidth(strokeWidth);
        popupUpdate();
    }

    public void setInactive() {
        if (this instanceof OutputPort &&  getConTo() != null) {
            this.conTo.setInactive();
        }
        double strokeWidth = this.shape.getStrokeWidth();
        this.shape.setStroke(stColor);
        this.shape.setStrokeWidth(strokeWidth);
        popupUpdate();
    }

    public Rectangle getVisuals() {
        return this.shape;
    }

    public void setVisuals(double X, double Y) {
        this.shape = new Rectangle(this.sizeX, this.sizeY, Color.GOLD);
        this.shape.setStroke(stColor);
        this.shape.setStrokeWidth(2);

        this.layoutX = X;
        this.layoutY = Y;

        this.shape.setX(X - this.shape.getWidth() / 2);
        this.shape.setY(Y - this.shape.getHeight() / 2);

        this.shape.setOnMouseClicked(e -> this.logic.portClick(this, e));
        this.shape.setOnMouseEntered(e -> this.logic.elementHover(e));
        this.shape.setOnMouseExited(e -> this.logic.elementHover(e));
        popupUpdate();
    }

    public Connection getConTo() {
        return this.conTo;
    }

    public void set() {
        this.stack.getChildren().add(this.shape);
    }

    public void remove() {
        if (isConnected()) this.conTo.remove();
        Platform.runLater(() -> this.stack.getChildren().clear());
    }

    public Block getParent() {
        return this.parent;
    }

    public double getCenterX() {
        return this.parent.getVisuals().getLayoutX() + this.shape.getX() + this.shape.getWidth() / 2;
    }

    public double getCenterY() {
        return this.parent.getVisuals().getLayoutY() + this.shape.getY() + this.shape.getHeight() / 2;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void reposition() {
        if (isConnected()) this.conTo.reposition(this);
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }

    public void popupUpdate() {
        String info = "";
        info += "ID: " + getId() + "\n";
        info += "Block ID: " + getParent().getId() + "\n";
        info += "Connected: " + isConnected();
        Tooltip popupMsg = new Tooltip(info);
        Tooltip.install(this.shape, popupMsg);
    }

    public abstract void createSave(ItemContainer container);

}
