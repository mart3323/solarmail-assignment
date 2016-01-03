package solarpost.code.ship;

import solarpost.interfaces.ship.IHullProfile;
import solarpost.interfaces.station.IPostOffice;

import java.util.function.Function;

/**
 * A consumer-grade hull with heat plating for landing on Hot planets
 * <p> While this ship is capable of surviving in Hot environments,
 * it does consume more fuel and has a reduced cargo hold
 * <table>
 *     <tr>
 *         <td valign=top>
 *            <ol>
 *                <lh>Temperature classes</lh>
 *                <li style=color:#ff5522>Cold</li>
 *                <li style=color:#44ff44>Normal</li>
 *                <li style=color:#44ff44>Hot</li>
 *            </ol>
 *            <table>
 *                <tr>
 *                    <th>
 *                        <th>capacity</th>
 *                        <th colspan=2>consumption/wear on launch<br>Cold|Normal|Hot</th>
 *                        <th>Launches on full<br>Cold|Normal|Hot
 *                    </th>
 *                </tr>
 *                <tr>
 *                    <th>Fuel:</th><td>100</td><td>X|25|50</td><td>X|4|2</td>
 *                </tr>
 *
 *                <tr>
 *                    <th>Scanner:</th><td>100</td><td>4</td><td>25</td>
 *                </tr>
 *            </table>
 *         </td>
 *         <td>
 *             <img style=float:right src="../../images/diamondback_scout.png">
 *         </td>
 *     </tr>
 * </table>
 */
public class HeatShieldedHull implements IHullProfile {

    @Override public int getMaxFuel() { return 100; }
    @Override public int getMaxScanner() { return 100; }
    @Override public int getCargoCapacity() { return 80; }

    @Override
    public Function<IPostOffice, Integer> getScannerWearPattern() {
        return p -> 4;
    }

    @Override
    public Function<IPostOffice, Integer> getFuelConsumptionPattern() {
        return p -> {
            switch (p.getTempClass()) {
                case Hot:
                    return 50;
                case Normal:
                    return 25;
                default:
                    return getMaxFuel()+1;
            }
        };
    }
}
