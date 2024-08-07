package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;

public class Bulldozer extends Actor implements Machine
{
    private int ID;
    private List<String> controls = null;
    private int autoMovementIndex = 0;
    private int movesCount = 0;
    private int itemsRemovedCount = 0;
    private OreSim oreSim;

    public Bulldozer(OreSim oreSim, int ID)
    {
        super(true, "sprites/bulldozer.png");  // Rotatable
        this.oreSim = oreSim;
        this.ID = ID;
    }
    public void setupBulldozer(boolean isAutoMode, List<String> controls) {
        this.controls = controls;
    }

    /**
     * Method to move bulldozer automatically based on the instructions input from properties file
     */
    public void autoMoveNext(boolean isFinished) {
        if (controls != null && autoMovementIndex < controls.size()) {
            String[] currentMove = controls.get(autoMovementIndex).split("-");
            String machine = currentMove[0];
            String move = currentMove[1];
            autoMovementIndex++;
            if (machine.equals("B")) {
                if (isFinished)
                    return;

                Location next = null;
                switch (move)
                {
                    case "L":
                        next = getLocation().getNeighbourLocation(Location.WEST);
                        setDirection(Location.WEST);
                        break;
                    case "U":
                        next = getLocation().getNeighbourLocation(Location.NORTH);
                        setDirection(Location.NORTH);
                        break;
                    case "R":
                        next = getLocation().getNeighbourLocation(Location.EAST);
                        setDirection(Location.EAST);
                        break;
                    case "D":
                        next = getLocation().getNeighbourLocation(Location.SOUTH);
                        setDirection(Location.SOUTH);
                        break;
                }

                if (next != null && canMove(next))
                {
                    setLocation(next);
                    movesCount++;
                }
                oreSim.refresh();
            }
        }
    }

    /**
     * Check if we can move the bulldozer into the location
     * @param location
     * @return
     */
    public boolean canMove(Location location)
    {
        // Test where it is trying to move to
        Color c = oreSim.getBg().getColor(location);
        Rock rock = (Rock) oreSim.getOneActorAt(location, Rock.class);
        Ore ore = (Ore) oreSim.getOneActorAt(location, Ore.class);
        Pusher pusher = (Pusher) oreSim.getOneActorAt(location, Pusher.class);
        Excavator excavator = (Excavator) oreSim.getOneActorAt(location, Excavator.class);
        if (c.equals(oreSim.borderColor) || rock != null || ore != null || pusher != null || excavator != null)
            return false;
        else // Test if there is a clay
        {
            Clay clay = (Clay) oreSim.getOneActorAt(location, Clay.class);
            if (clay != null)
            {

                // remove clay
                clay.removeSelf();
                itemsRemovedCount++;
            }
        }

        return true;
    }

    // added new getters

    public int getMovesCount() {
        return movesCount;
    }

    public int getItemsRemovedCount() {
        return itemsRemovedCount;
    }

    public int getID() {
        return ID;
    }
}
