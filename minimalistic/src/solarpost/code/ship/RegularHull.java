package solarpost.code.ship;

import solarpost.interfaces.ship.IHullProfile;
import solarpost.interfaces.station.IPostOffice;

import java.util.function.Function;

/**
 * A consumer-grade hull that provides average protection
 * <p> With a reasonably large cargo hold and mild fuel consumption,
 * this ship is great for transport between habitable planets
 *   <table>
 *       <tr>
 *           <td valign=top>
 *               <ol>
 *                   <lh>Temperature classes</lh>
 *                   <li style=color:#ff5522>Cold</li>
 *                   <li style=color:#44ff44>Normal</li>
 *                   <li style=color:#ff5522>Hot</li>
 *               </ol>
 *               <table>
 *                   <tr>
 *                       <th></th> <th>capacity</th> <th colspan=1>consumption/wear on launch</th><th>Launches on full</th>
 *                   </tr>
 *                   <tr>
 *                       <th>Fuel:</th><td>100</td><td>20</td><td>5</td>
 *                   </tr>
 *
 *                   <tr>
 *                       <th>Scanner:</th><td>100</td><td>4</td><td>25</td>
 *                   </tr>
 *               </table>
 *            </td>
 *            <td>
 *                <img style=float:right src="../../images/FederalDropship.jpg">
 *            </td>
 *        </tr>
 *  </table>
 */
public class RegularHull implements IHullProfile {

    @Override public int getMaxFuel() { return 100; }
    @Override public int getMaxScanner() { return 100; }
    @Override public int getCargoCapacity() { return 100; }

    @Override
    public Function<IPostOffice, Integer> getScannerWearPattern() {
        return p -> 4;
    }

    @Override
    public Function<IPostOffice, Integer> getFuelConsumptionPattern() {
        return p -> {
            switch (p.getTempClass()) {
                case Normal:
                    return 20;
                default:
                    return getMaxFuel()+1;
            }
        };
    }

}
