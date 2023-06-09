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
package quests.Q168_DeliverSupplies;

import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.quest.State;

public class Q168_DeliverSupplies extends Quest
{
	// Items
	private static final int JENNA_LETTER = 1153;
	private static final int SENTRY_BLADE_1 = 1154;
	private static final int SENTRY_BLADE_2 = 1155;
	private static final int SENTRY_BLADE_3 = 1156;
	private static final int OLD_BRONZE_SWORD = 1157;
	
	// NPCs
	private static final int JENNA = 7349;
	private static final int ROSELYN = 7355;
	private static final int KRISTIN = 7357;
	private static final int HARANT = 7360;

	public static void main(String[] args)
	{
		new Q168_DeliverSupplies();
	}
	
	public Q168_DeliverSupplies()
	{
		super(168, Q168_DeliverSupplies.class.getSimpleName(), "Deliver Supplies");
		
		setItemsIds(JENNA_LETTER, SENTRY_BLADE_1, SENTRY_BLADE_2, SENTRY_BLADE_3, OLD_BRONZE_SWORD);
		
		addStartNpc(JENNA);
		addTalkId(JENNA, ROSELYN, KRISTIN, HARANT);
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("7349-03.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(JENNA_LETTER, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case State.CREATED:
				if (player.getRace() != Race.DARK_ELF)
					htmltext = "7349-00.htm";
				else if (player.getLevel() < 3)
					htmltext = "7349-01.htm";
				else
					htmltext = "7349-02.htm";
				break;
			
			case State.STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case JENNA:
						if (cond == 1)
							htmltext = "7349-04.htm";
						else if (cond == 2)
						{
							htmltext = "7349-05.htm";
							st.set("cond", "3");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(SENTRY_BLADE_1, 1);
						}
						else if (cond == 3)
							htmltext = "7349-07.htm";
						else if (cond == 4)
						{
							htmltext = "7349-06.htm";
							st.takeItems(OLD_BRONZE_SWORD, 2);
							st.rewardItems(57, 820);
							st.playSound(QuestState.SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case HARANT:
						if (cond == 1)
						{
							htmltext = "7360-01.htm";
							st.set("cond", "2");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(JENNA_LETTER, 1);
							st.giveItems(SENTRY_BLADE_1, 1);
							st.giveItems(SENTRY_BLADE_2, 1);
							st.giveItems(SENTRY_BLADE_3, 1);
						}
						else if (cond == 2)
							htmltext = "7360-02.htm";
						break;
					
					case ROSELYN:
						if (cond == 3)
						{
							if (st.hasQuestItems(SENTRY_BLADE_2))
							{
								htmltext = "7355-01.htm";
								st.takeItems(SENTRY_BLADE_2, 1);
								st.giveItems(OLD_BRONZE_SWORD, 1);
								if (st.getQuestItemsCount(OLD_BRONZE_SWORD) == 2)
								{
									st.set("cond", "4");
									st.playSound(QuestState.SOUND_MIDDLE);
								}
							}
							else
								htmltext = "7355-02.htm";
						}
						else if (cond == 4)
							htmltext = "7355-02.htm";
						break;
					
					case KRISTIN:
						if (cond == 3)
						{
							if (st.hasQuestItems(SENTRY_BLADE_3))
							{
								htmltext = "7357-01.htm";
								st.takeItems(SENTRY_BLADE_3, 1);
								st.giveItems(OLD_BRONZE_SWORD, 1);
								if (st.getQuestItemsCount(OLD_BRONZE_SWORD) == 2)
								{
									st.set("cond", "4");
									st.playSound(QuestState.SOUND_MIDDLE);
								}
							}
							else
								htmltext = "7357-02.htm";
						}
						else if (cond == 4)
							htmltext = "7357-02.htm";
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