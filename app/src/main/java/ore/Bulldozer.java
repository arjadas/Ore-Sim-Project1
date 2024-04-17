package ore;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.awt.*;
import java.util.List;

/*
public class Bulldozer implements Machine {
}*/

public class Bulldozer extends Actor implements Machine
{
    private List<String> controls = null;
    private int autoMovementIndex = 0;

    private OreSim oreSim;

    public Bulldozer(OreSim oreSim)
    {
        super(true, "sprites/bulldozer.png");  // Rotatable
        this.oreSim = oreSim;
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

                Target curTarget = (Target) oreSim.getOneActorAt(getLocation(), Target.class);
                if (curTarget != null){
                    curTarget.show();
                }
                if (next != null && canMove(next))
                {
                    setLocation(next);
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
        // Test if try to move into border, rock or clay
        Color c = oreSim.getBg().getColor(location);
        Rock rock = (Rock) oreSim.getOneActorAt(location, Rock.class);
        Clay clay = (Clay) oreSim.getOneActorAt(location, Clay.class);
        Pusher pusher = (Pusher) oreSim.getOneActorAt(location, Pusher.class);
        Excavator excavator = (Excavator) oreSim.getOneActorAt(location, Excavator.class);
        if (c.equals(oreSim.borderColor) || rock != null || clay != null || pusher != null || excavator != null)
            return false;
        else // Test if there is an ore
        {
            Ore ore = (Ore) oreSim.getOneActorAt(location, Ore.class);
            if (ore != null)
            {

                // Try to move the ore
                ore.setDirection(getDirection());
                if (ore.moveOre(oreSim))
                    return true;
                else
                    return false;

            }
        }

        return true;
    }
}
