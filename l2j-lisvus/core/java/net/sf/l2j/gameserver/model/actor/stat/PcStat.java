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
package net.sf.l2j.gameserver.model.actor.stat;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.PetDataTable;
import net.sf.l2j.gameserver.model.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2ClassMasterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

public class PcStat extends PlayableStat
{
	// =========================================================
	// Data Field
	
	// =========================================================
	// Constructor
	public PcStat(L2PcInstance activeChar)
	{
		super(activeChar);
	}
	
	// =========================================================
	// Method - Public
	@Override
	public boolean addExp(long value)
	{
		// Prevent only gaining experience
		if (!getActiveChar().getGainXpSp() && (value > 0))
		{
			return false;
		}

		if (!super.addExp(value))
		{
			return false;
		}

		// Set new karma
		if ((getActiveChar().getKarma() > 0) && (getActiveChar().isGM() || !getActiveChar().isInsideZone(L2Character.ZONE_PVP)))
		{
			int karmaLost = getActiveChar().calculateKarmaLost(value);
			if (karmaLost > 0)
			{
				getActiveChar().setKarma(getActiveChar().getKarma() - karmaLost);
			}
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.EXP, Experience.getVisualExp(getLevel(), getExp()));
		getActiveChar().sendPacket(su);
		
		return true;
	}
	
	/**
	 * Add Experience and SP rewards to the L2PcInstance, remove its Karma (if necessary) and Launch increase level task.<BR>
	 * <BR>
	 * <B><U> Actions </U> :</B><BR>
	 * <BR>
	 * <li>Remove Karma when the player kills L2MonsterInstance</li>
	 * <li>Send a Server->Client packet StatusUpdate to the L2PcInstance</li>
	 * <li>Send a Server->Client System Message to the L2PcInstance</li>
	 * <li>If the L2PcInstance increases it's level, send a Server->Client packet SocialAction (broadcast)</li>
	 * <li>If the L2PcInstance increases it's level, manage the increase level task (Max MP, Max MP, Recommendation, Expertise and beginner skills...)</li>
	 * <li>If the L2PcInstance increases it's level, send a Server->Client packet UserInfo to the L2PcInstance</li><BR>
	 * <BR>
	 * @param addToExp The Experience value to add
	 * @param addToSp The SP value to add
	 */
	@Override
	public boolean addExpAndSp(long addToExp, int addToSp)
	{
		float ratioTakenByPet = 0;
		
		// if this player has a pet that gains from the owner's Exp, give the pet Exp now
		if ((getActiveChar().getPet() != null) && (getActiveChar().getPet() instanceof L2PetInstance))
		{
			L2PetInstance pet = (L2PetInstance) getActiveChar().getPet();
			ratioTakenByPet = pet.getPetData().getOwnerExpTaken();
			
			// only give exp/sp to the pet by taking from the owner if the pet has a non-zero, positive ratio
			// allow possible customizations that would have the pet earning more than 100% of the owner's exp/sp
			if ((ratioTakenByPet > 0) && !pet.isDead())
			{
				pet.addExpAndSp((long) (addToExp * ratioTakenByPet), (int) (addToSp * ratioTakenByPet));
			}
			
			// now adjust the max ratio to avoid the owner earning negative exp/sp
			if (ratioTakenByPet > 1)
			{
				ratioTakenByPet = 1;
			}
			
			addToExp = (long) (addToExp * (1 - ratioTakenByPet));
			addToSp = (int) (addToSp * (1 - ratioTakenByPet));
		}
		
		if (!super.addExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		// Send a Server->Client System Message to the L2PcInstance
		SystemMessage sm = new SystemMessage(SystemMessage.YOU_EARNED_S1_EXP_AND_S2_SP);
		sm.addNumber((int) addToExp);
		sm.addNumber(addToSp);
		getActiveChar().sendPacket(sm);
		
		return true;
	}
	
	@Override
	public boolean removeExpAndSp(long addToExp, int addToSp)
	{
		return removeExpAndSp(addToExp, addToSp, true);
	}
	
	public boolean removeExpAndSp(long addToExp, int addToSp, boolean sendMessage)
	{
		if (!super.removeExpAndSp(addToExp, addToSp))
		{
			return false;
		}
		
		if (sendMessage)
		{
			// Send a Server->Client System Message to the L2PcInstance
			SystemMessage sm = new SystemMessage(SystemMessage.EXP_DECREASED_BY_S1);
			sm.addNumber((int) addToExp);
			getActiveChar().sendPacket(sm);
			sm = new SystemMessage(SystemMessage.SP_DECREASED_S1);
			sm.addNumber(addToSp);
			getActiveChar().sendPacket(sm);
		}
		return true;
	}
	
	@Override
	public final boolean addLevel(byte value)
	{
		if ((getLevel() + value) > (Config.MAX_PLAYER_LEVEL - 1))
		{
			return false;
		}
		
		boolean levelIncreased = super.addLevel(value);
		if (levelIncreased)
		{
			// Class change
			L2ClassMasterInstance.showQuestionMark(getActiveChar());
			
			// Tutorial guide
			QuestState qs = getActiveChar().getQuestState("255_Tutorial");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("CE40", null, getActiveChar());
			}
			
			// Novice status
			if (getActiveChar().isNewbie() && getActiveChar().getNewbieAt() <= 0)
			{
				getActiveChar().setNewbieAt(System.currentTimeMillis());
			}
			
			getActiveChar().setCurrentCp(getMaxCp());
			getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), 15));
			getActiveChar().sendPacket(new SystemMessage(SystemMessage.YOU_INCREASED_YOUR_LEVEL));
		}
		
		getActiveChar().rewardSkills(); // Give Expertise skill of this level
		
		if (getActiveChar().getClan() != null)
		{
			getActiveChar().getClan().addClanMember(getActiveChar());
			getActiveChar().getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(getActiveChar()), getActiveChar());
		}
		
		if (getActiveChar().isInParty())
		{
			getActiveChar().getParty().recalculatePartyLevel(); // Recalculate the party level
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.LEVEL, getLevel());
		su.addAttribute(StatusUpdate.MAX_CP, getMaxCp());
		su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
		su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
		getActiveChar().sendPacket(su);
		
		// Update the overloaded status of the L2PcInstance
		getActiveChar().refreshOverloaded();
		// Update the expertise status of the L2PcInstance
		getActiveChar().refreshExpertisePenalty();
		// Send a Server->Client packet UserInfo to the L2PcInstance
		getActiveChar().sendPacket(new UserInfo(getActiveChar()));
		
		return levelIncreased;
	}
	
	@Override
	public boolean addSp(int value)
	{
		if (!getActiveChar().getGainXpSp() && (value > 0))
		{
			return false;
		}
		
		if (!super.addSp(value))
		{
			return false;
		}
		
		StatusUpdate su = new StatusUpdate(getActiveChar().getObjectId());
		su.addAttribute(StatusUpdate.SP, getSp());
		getActiveChar().sendPacket(su);
		
		return true;
	}
	
	@Override
	public final long getExpForLevel(int level)
	{
		return Experience.LEVEL[level];
	}
	
	// =========================================================
	// Method - Private
	
	// =========================================================
	// Property - Public
	@Override
	public final L2PcInstance getActiveChar()
	{
		return (L2PcInstance) super.getActiveChar();
	}

	@Override
	public final long getExp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getExp();
		}
		
		return super.getExp();
	}
	
	@Override
	public final void setExp(long value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setExp(value);
		}
		else
		{
			super.setExp(value);
		}
	}
	
	@Override
	public final byte getLevel()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getLevel();
		}
		
		return super.getLevel();
	}
	
	@Override
	public final void setLevel(byte value)
	{
		if (value > (Config.MAX_PLAYER_LEVEL - 1))
		{
			value = (byte) (Config.MAX_PLAYER_LEVEL - 1);
		}
		
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setLevel(value);
		}
		else
		{
			super.setLevel(value);
		}
	}
	
	@Override
	public final int getSp()
	{
		if (getActiveChar().isSubClassActive())
		{
			return getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).getSp();
		}
		
		return super.getSp();
	}
	
	@Override
	public final void setSp(int value)
	{
		if (getActiveChar().isSubClassActive())
		{
			getActiveChar().getSubClasses().get(getActiveChar().getClassIndex()).setSp(value);
		}
		else
		{
			super.setSp(value);
		}
	}
	
	@Override
	public int getBaseRunSpeed()
	{
		L2PcInstance player = getActiveChar();
		// Base swimming speed
		if (player.isInsideZone(L2Character.ZONE_WATER))
		{
			return player.isFlying() ? 140 : (player.isMounted() ? 70 : 50);
		}
		
		if (player.isMounted())
		{
			return PetDataTable.getInstance().getPetData(player.getMountNpcId(), player.getMountLevel()).getPetSpeed();
		}
		
		return super.getBaseRunSpeed();
	}
	
	@Override
	public int getBaseWalkSpeed()
	{
		L2PcInstance player = getActiveChar();
		// Base swimming speed
		if (player.isInsideZone(L2Character.ZONE_WATER))
		{
			return player.isFlying() ? 140 : (player.isMounted() ? 70 : 50);
		}
		
		if (player.isMounted())
		{
			return PetDataTable.getInstance().getPetData(player.getMountNpcId(), player.getMountLevel()).getPetSpeed() / 2;
		}
		
		return super.getBaseWalkSpeed();
	}
}