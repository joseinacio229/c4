/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver;

import java.lang.reflect.Constructor;

import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.templates.L2NpcTemplate;
import net.sf.l2j.util.Rnd;

public class MonsterRace
{
	private final L2NpcInstance[] monsters;
    private final int[][] speeds;
    private final int[] first, second; 
    
    private MonsterRace()
    {
        monsters    = new L2NpcInstance[8];
        speeds      = new int[8][20];
        first       = new int[2];
        second      = new int[2];
    }
    
    public static MonsterRace getInstance()
    {
        return SingletonHolder._instance;
    }
    
    public void newRace()
    {
        int random = 0;
        
        for (int i=0; i<8; i++)
        {
            int id = 8003;
            random = Rnd.get(24);
            while(true)
            {
                for (int j=i-1; j>=0; j--)
                {
                    if (monsters[j].getTemplate().npcId == (id + random))
                    {
                        random = Rnd.get(24);
                        continue;
                    }
                }
                break;
            }
            try
            {
                L2NpcTemplate template = NpcTable.getInstance().getTemplate(id+random);
                Constructor<?> _constructor = Class.forName("net.sf.l2j.gameserver.model.actor.instance." + template.type + "Instance").getConstructors()[0];
                int objectId = IdFactory.getInstance().getNextId();
                monsters[i] = (L2NpcInstance)_constructor.newInstance(objectId, template);
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        newSpeeds();
    }
    
    public void newSpeeds()
    {
        int total = 0; first[1]=0; second[1]=0;

        for (int i=0; i<8; i++)
        {
            total = 0;
            for (int j=0; j<20 ;j++)
            {
                if (j == 19)
                    speeds[i][j] = 100;
                else
                    speeds[i][j] = Rnd.get(60) + 65;
                total += speeds[i][j];
            }

            if (total >= first[1])
            {
                second[0] = first[0];
                second[1] = first[1];
                first[0] = 8 - i;
                first[1] = total;
            } 
            else if (total >= second[1])
            {
                second[0] = 8 - i;
                second[1] = total;
            }
        }
    }

    /**
     * @return Returns the monsters.
     */
    public L2NpcInstance[] getMonsters()
    {
        return monsters;
    }

    /**
     * @return Returns the speeds.
     */
    public int[][] getSpeeds()
    {
        return speeds;
    }

    public int getFirstPlace()
    {
        return first[0];
    }

    public int getSecondPlace()
    {
        return second[0];
    }
    
    private static class SingletonHolder
	{
		protected static final MonsterRace _instance = new MonsterRace();
	}
}