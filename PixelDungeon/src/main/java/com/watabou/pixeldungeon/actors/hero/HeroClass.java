/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.pixeldungeon.actors.hero;

import com.nyrds.android.util.TrackedRuntimeException;
import com.nyrds.pixeldungeon.items.chaos.ChaosArmor;
import com.nyrds.pixeldungeon.items.chaos.ChaosCrystal;
import com.nyrds.pixeldungeon.items.guts.armor.GothicArmor;
import com.nyrds.pixeldungeon.items.guts.weapon.melee.Claymore;
import com.nyrds.pixeldungeon.items.guts.weapon.melee.Halberd;
import com.nyrds.pixeldungeon.items.guts.weapon.ranged.CompositeCrossbow;
import com.nyrds.pixeldungeon.items.guts.weapon.ranged.RubyCrossbow;
import com.nyrds.pixeldungeon.items.guts.weapon.ranged.WoodenCrossbow;
import com.nyrds.pixeldungeon.ml.BuildConfig;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.items.TomeOfMastery;
import com.watabou.pixeldungeon.items.armor.ClassArmor;
import com.watabou.pixeldungeon.items.armor.ClothArmor;
import com.watabou.pixeldungeon.items.armor.ElfArmor;
import com.watabou.pixeldungeon.items.armor.HuntressArmor;
import com.watabou.pixeldungeon.items.armor.MageArmor;
import com.watabou.pixeldungeon.items.armor.RogueArmor;
import com.watabou.pixeldungeon.items.armor.WarriorArmor;
import com.watabou.pixeldungeon.items.food.Ration;
import com.watabou.pixeldungeon.items.potions.PotionOfStrength;
import com.watabou.pixeldungeon.items.potions.PotionOfToxicGas;
import com.watabou.pixeldungeon.items.rings.RingOfShadows;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.items.wands.WandOfMagicMissile;
import com.watabou.pixeldungeon.items.weapon.melee.Dagger;
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles;
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword;
import com.watabou.pixeldungeon.items.weapon.melee.WoodenBow;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.items.weapon.missiles.CommonArrow;
import com.watabou.pixeldungeon.items.weapon.missiles.Dart;
import com.watabou.pixeldungeon.ui.QuickSlot;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Bundle;

public enum HeroClass {

	WARRIOR(Game.getVar(R.string.HeroClass_War),WarriorArmor.class),
	MAGE(Game.getVar(R.string.HeroClass_Mag),MageArmor.class),
	ROGUE(Game.getVar(R.string.HeroClass_Rog),RogueArmor.class),
	HUNTRESS(Game.getVar(R.string.HeroClass_Hun),HuntressArmor.class),
	ELF(Game.getVar(R.string.HeroClass_Elf),ElfArmor.class);

	private final Class<? extends ClassArmor> armorClass;

	private String title;

	public static final String[] WAR_PERKS = Game
			.getVars(R.array.HeroClass_WarPerks);
	public static final String[] MAG_PERKS = Game
			.getVars(R.array.HeroClass_MagPerks);
	public static final String[] ROG_PERKS = Game
			.getVars(R.array.HeroClass_RogPerks);
	public static final String[] HUN_PERKS = Game
			.getVars(R.array.HeroClass_HunPerks);
	public static final String[] ELF_PERKS = Game
			.getVars(R.array.HeroClass_ElfPerks);

	HeroClass(String title, Class<? extends ClassArmor> armorClass) {
		this.title = title;
		this.armorClass = armorClass;
	}

	public void initHero(Hero hero) {
		hero.heroClass = this;
		initCommon(hero);

		switch (this) {
		case WARRIOR:
			initWarrior(hero);
			break;

		case MAGE:
			initMage(hero);
			break;

		case ROGUE:
			initRogue(hero);
			break;

		case HUNTRESS:
			initHuntress(hero);
			break;

		case ELF:
			initElf(hero);
			break;
		}

		hero.setGender(getGender());

		if (Badges.isUnlocked(masteryBadge()) && hero.getDifficulty() < 3) {
			new TomeOfMastery().collect(hero);
		}

		hero.updateAwareness();
	}

	private static void initDebug(Hero hero) {
		for(int i = 0;i<100;i++) {
			hero.collect(new ScrollOfMagicMapping());
			hero.collect(new PotionOfToxicGas());
		}

		Badges.validateBossSlain(Badges.Badge.BOSS_SLAIN_3);

		hero.collect(new Claymore().upgrade(100));
		hero.collect(new GothicArmor());
		hero.collect(new ChaosArmor());
		hero.collect(new Halberd().upgrade(100));
		hero.collect(new WoodenCrossbow());
		hero.collect(new RubyCrossbow());
		hero.collect(new CompositeCrossbow());

		hero.collect(new ChaosCrystal().identify().identify());
		hero.ht(1000);
		hero.hp(1000);
		hero.attackSkill = 1000;
		hero.defenseSkill = 1000;

	}

	private static void initCommon(Hero hero) {
		(hero.belongings.armor = new ClothArmor()).identify();
		hero.collect(new Ration());
		if(BuildConfig.DEBUG) initDebug(hero);
		QuickSlot.cleanStorage();
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
		case WARRIOR:
			return Badges.Badge.MASTERY_WARRIOR;
		case MAGE:
			return Badges.Badge.MASTERY_MAGE;
		case ROGUE:
			return Badges.Badge.MASTERY_ROGUE;
		case HUNTRESS:
			return Badges.Badge.MASTERY_HUNTRESS;
		case ELF:
			return Badges.Badge.MASTERY_ELF;
		}
		return null;
	}

	private static void initWarrior(Hero hero) {
		hero.STR(hero.STR() + 1);

		(hero.belongings.weapon = new ShortSword()).identify();
		hero.collect(new Dart(8).identify());

		QuickSlot.selectItem(Dart.class, 0);

		new PotionOfStrength().setKnown();
	}

	private static void initMage(Hero hero) {
		(hero.belongings.weapon = new Knuckles()).identify();

		WandOfMagicMissile wand = new WandOfMagicMissile();
		hero.collect(wand.identify());

		QuickSlot.selectItem(wand, 0);

		new ScrollOfIdentify().setKnown();
	}

	private static void initRogue(Hero hero) {
		(hero.belongings.weapon = new Dagger()).identify();
		(hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();

		hero.collect(new Dart(8).identify());

		hero.belongings.ring1.activate(hero);

		QuickSlot.selectItem(Dart.class, 0);

		new ScrollOfMagicMapping().setKnown();
	}

	private static void initHuntress(Hero hero) {
		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.weapon = new Dagger()).identify();
		Boomerang boomerang = new Boomerang();
		hero.collect(boomerang.identify());

		QuickSlot.selectItem(boomerang, 0);
	}

	private void initElf(Hero hero) {
		hero.STR(hero.STR() - 1);

		hero.ht(hero.ht() - 5);
		hero.hp(hero.ht());

		(hero.belongings.armor = new ClothArmor()).upgrade().identify();
		(hero.belongings.weapon = new WoodenBow()).upgrade().identify();

		hero.collect(new Dagger().upgrade().identify());
		hero.collect(new CommonArrow(20));

		QuickSlot.selectItem(CommonArrow.class, 0);
	}

	public String title() {
		return title;
	}

	public String[] perks() {

		switch (this) {
		case WARRIOR:
			return WAR_PERKS;
		case MAGE:
			return MAG_PERKS;
		case ROGUE:
			return ROG_PERKS;
		case HUNTRESS:
			return HUN_PERKS;
		case ELF:
			return ELF_PERKS;
		}

		return null;
	}

	public int getGender() {
		switch (this) {
		case WARRIOR:
		case MAGE:
		case ROGUE:
		case ELF:
			return Utils.MASCULINE;
		case HUNTRESS:
			return Utils.FEMININE;
		}
		return Utils.NEUTER;
	}

	private static final String CLASS = "class";

	public void storeInBundle(Bundle bundle) {
		bundle.put(CLASS, toString());
	}

	public static HeroClass restoreFromBundle(Bundle bundle) {
		String value = bundle.getString(CLASS);
		return value.length() > 0 ? valueOf(value) : ROGUE;
	}

	public ClassArmor classArmor() {
		try {
			return armorClass.newInstance();
		} catch (Exception e) {
			throw new TrackedRuntimeException(e);
		}
	}
}
