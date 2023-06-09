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
package quests.Q101_SwordOfSolidarity;

import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.quest.State;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class Q101_SwordOfSolidarity extends Quest
{
	// NPCs
	private static final int ROIEN = 7008;
	private static final int ALTRAN = 7283;
	
	// Items
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIENS_LETTER = 796;
	private static final int DIR_TO_RUINS = 937;
	private static final int ALTRANS_NOTE = 742;
	
	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final int SPIRITSHOT_FOR_BEGINNERS = 5790;
	private static final int SOULSHOT_FOR_BEGINNERS = 5789;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public static void main(String[] args)
    {
        new Q101_SwordOfSolidarity();
    }
	
	public Q101_SwordOfSolidarity()
	{
		super(101, Q101_SwordOfSolidarity.class.getSimpleName(), "Sword of Solidarity");
		
		setItemsIds(BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP);
		
		addStartNpc(ROIEN);
		addTalkId(ROIEN, ALTRAN);
		
		addKillId(361, 362);
	}
	
	@Override
	public String onAdvEvent(String event, L2NpcInstance npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(getName());
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("7008-04.htm"))
		{
			st.setState(State.STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
			st.giveItems(ROIENS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("7283-02.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(ROIENS_LETTER, 1);
			st.giveItems(DIR_TO_RUINS, 1);
		}
		else if (event.equalsIgnoreCase("7283-07.htm"))
		{
			st.takeItems(BROKEN_SWORD_HANDLE, 1);
			st.giveItems(SWORD_OF_SOLIDARITY, 1);
			st.giveItems(LESSER_HEALING_POT, 100);
			
			if (player.isNewbie())
			{
				st.showQuestionMark(26);
				if (player.isMageClass())
				{
					st.playTutorialVoice("tutorial_voice_027");
					st.giveItems(SPIRITSHOT_FOR_BEGINNERS, 3000);
				}
				else
				{
					st.playTutorialVoice("tutorial_voice_026");
					st.giveItems(SOULSHOT_FOR_BEGINNERS, 7000);
				}
			}
			
			st.giveItems(ECHO_BATTLE, 10);
			st.giveItems(ECHO_LOVE, 10);
			st.giveItems(ECHO_SOLITUDE, 10);
			st.giveItems(ECHO_FEAST, 10);
			st.giveItems(ECHO_CELEBRATION, 10);
			player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
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
				if (player.getRace() != Race.HUMAN)
					htmltext = "7008-01.htm";
				else if (player.getLevel() < 9)
					htmltext = "7008-08.htm";
				else
					htmltext = "7008-02.htm";
				break;
			
			case State.STARTED:
				int cond = (st.getInt("cond"));
				switch (npc.getNpcId())
				{
					case ROIEN:
						if (cond == 1)
							htmltext = "7008-05.htm";
						else if (cond == 2)
							htmltext = "7008-10.htm";
						else if (cond == 3)
							htmltext = "7008-12.htm";
						else if (cond == 4)
						{
							htmltext = "7008-06.htm";
							st.set("cond", "5");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(ALTRANS_NOTE, 1);
							st.giveItems(BROKEN_SWORD_HANDLE, 1);
						}
						else if (cond == 5)
							htmltext = "7008-07.htm";
						break;
					
					case ALTRAN:
						if (cond == 1)
							htmltext = "7283-01.htm";
						else if (cond == 2)
							htmltext = "7283-03.htm";
						else if (cond == 3)
						{
							htmltext = "7283-04.htm";
							st.set("cond", "4");
							st.playSound(QuestState.SOUND_MIDDLE);
							st.takeItems(DIR_TO_RUINS, 1);
							st.takeItems(BROKEN_BLADE_TOP, 1);
							st.takeItems(BROKEN_BLADE_BOTTOM, 1);
							st.giveItems(ALTRANS_NOTE, 1);
						}
						else if (cond == 4)
							htmltext = "7283-05.htm";
						else if (cond == 5)
							htmltext = "7283-06.htm";
						break;
				}
				break;
			
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2NpcInstance npc, L2PcInstance killer, boolean isPet)
	{
		final QuestState st = checkPlayerCondition(killer, npc, "cond", "2");
		if (st == null)
			return null;
		
		if (!st.hasQuestItems(BROKEN_BLADE_TOP))
			st.dropItems(BROKEN_BLADE_TOP, 1, 1, 200000);
		else if (st.dropItems(BROKEN_BLADE_BOTTOM, 1, 1, 200000))
			st.set("cond", "3");
		
		return null;
	}
}