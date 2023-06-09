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
package quests.Q006_StepIntoTheFuture;

import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.quest.State;

public class Q006_StepIntoTheFuture extends Quest
{
    // NPCs
    private static final int ROXXY = 7006;
    private static final int BAULRO = 7033;
    private static final int SIR_COLLIN = 7311;
    
    // Items
    private static final int BAULRO_LETTER = 7571;
    
    // Rewards
    private static final int MARK_TRAVELER = 7570;
    private static final int SOE_GIRAN = 7559;

    public static void main(String[] args)
    {
        new Q006_StepIntoTheFuture();
    }
    
    public Q006_StepIntoTheFuture()
    {
        super(6, Q006_StepIntoTheFuture.class.getSimpleName(), "Step into the Future");
        
        setItemsIds(BAULRO_LETTER);
        
        addStartNpc(ROXXY);
        addTalkId(ROXXY, BAULRO, SIR_COLLIN);
    }
    
    @Override
    public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
    {
        String htmltext = event;
        QuestState st = player.getQuestState(getName());
        if (st == null)
            return htmltext;
        
        if (event.equalsIgnoreCase("7006-03.htm"))
        {
            st.setState(State.STARTED);
            st.set("cond", "1");
            st.playSound(QuestState.SOUND_ACCEPT);
        }
        else if (event.equalsIgnoreCase("7033-02.htm"))
        {
            st.set("cond", "2");
            st.playSound(QuestState.SOUND_MIDDLE);
            st.giveItems(BAULRO_LETTER, 1);
        }
        else if (event.equalsIgnoreCase("7311-02.htm"))
        {
            if (st.hasQuestItems(BAULRO_LETTER))
            {
                st.set("cond", "3");
                st.playSound(QuestState.SOUND_MIDDLE);
                st.takeItems(BAULRO_LETTER, 1);
            }
            else
                htmltext = "7311-03.htm";
        }
        else if (event.equalsIgnoreCase("7006-06.htm"))
        {
            st.giveItems(MARK_TRAVELER, 1);
            st.rewardItems(SOE_GIRAN, 1);
            st.playSound(QuestState.SOUND_FINISH);
            st.exitQuest(false);
        }
        
        return htmltext;
    }
    
    @Override
    public String onTalk(L2NpcInstance npc, L2PcInstance player)
    {
        QuestState st = player.getQuestState(getName());
        String htmltext = getNoQuestMsg();
        if (st == null)
            return htmltext;
        
        switch (st.getState())
        {
            case State.CREATED:
                if (player.getRace() != Race.HUMAN || player.getLevel() < 3)
                    htmltext = "7006-01.htm";
                else
                    htmltext = "7006-02.htm";
                break;
            
            case State.STARTED:
                int cond = st.getInt("cond");
                switch (npc.getNpcId())
                {
                    case ROXXY:
                        if (cond == 1 || cond == 2)
                            htmltext = "7006-04.htm";
                        else if (cond == 3)
                            htmltext = "7006-05.htm";
                        break;
                    
                    case BAULRO:
                        if (cond == 1)
                            htmltext = "7033-01.htm";
                        else if (cond == 2)
                            htmltext = "7033-03.htm";
                        else
                            htmltext = "7033-04.htm";
                        break;
                    
                    case SIR_COLLIN:
                        if (cond == 2)
                            htmltext = "7311-01.htm";
                        else if (cond == 3)
                            htmltext = "7311-03a.htm";
                        break;
                }
                break;
            
            case State.COMPLETED:
                htmltext = getAlreadyCompletedMsg();
                break;
        }
        
        return htmltext;
    }
}
