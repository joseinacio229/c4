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
package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * A mother-trees zone
 *
 * @author  durgus
 */
public class L2MotherTreeZone extends L2ZoneType
{
    public L2MotherTreeZone(int id)
    {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character)
    {
        if (character instanceof L2PcInstance)
        {
            L2PcInstance player = (L2PcInstance)character;

            if (player.isInParty())
            {
                for (L2PcInstance member : player.getParty().getPartyMembers())
                {
                    if (member.getRace() != Race.ELF)
                        return;
                }
            }

            player.setInsideZone(L2Character.ZONE_MOTHER_TREE, true);
            player.sendPacket(new SystemMessage(SystemMessage.ENTER_SHADOW_MOTHER_TREE));
        }
    }

    @Override
    protected void onExit(L2Character character)
    {
        if (character instanceof L2PcInstance && character.isInsideZone(L2Character.ZONE_MOTHER_TREE))
        {
            character.setInsideZone(L2Character.ZONE_MOTHER_TREE, false);
            ((L2PcInstance)character).sendPacket(new SystemMessage(SystemMessage.EXIT_SHADOW_MOTHER_TREE));
        }
    }
}