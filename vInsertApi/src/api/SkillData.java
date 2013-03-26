package api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.tools.Skills;



/**
 * This keeps track of the level and experience of each skill upon instantiation and offers various operations.
 *
 * @author Aion
 */
public final class SkillData {

	public static final int NUM_SKILL = 23;
	public final int[] initialExp = new int[NUM_SKILL];
	public final int[] initialLevels = new int[NUM_SKILL];

	private Skills skills;

	private Timer timer;

	public static enum Rate {
		MINUTE(60000d),
		HOUR(3600000d),
		DAY(86400000d),
		WEEK(604800000d);

		public final double time;

		Rate(final double time) {
			this.time = time;
		}

		/**
		 * Gets the time for this rate.
		 *
		 * @return this rate's time
		 */
		public double getTime() {
			return time;
		}
	}

	public SkillData(ScriptContext context) {
		this(new Timer(0l), context);
	}

	public int getLevel(int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return skills.getLevel(index);
	}

	public SkillData(final Timer timer, ScriptContext context) {
		this.skills = context.skills;
		
		for (int index = 0; index < NUM_SKILL; index++) {
			initialExp[index] = skills.getExperience(index);
			initialLevels[index] = getLevel(index);
		}
		this.timer = timer == null ? new Timer(0l) : timer;
	}

	/**
	 * Calculates the experience gained for the given skill index.
	 *
	 * @param index the skill index
	 * @return the experience gained
	 */
	public int experience(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return skills.getExperience(index) - initialExp[index];
	}

	/**
	 * Calculates the experience gained for the given skill index at the given rate.
	 *
	 * @param rate the rate in which to calculate the experience gained
	 * @param index the skill index
	 * @return the experience gained at the given rate
	 */
	public int experience(final Rate rate, final int index) {
		return (int) (experience(index) * rate.time / timer.getElapsed());
	}

	/**
	 * Calculates the number of levels gained for the given skill index.
	 *
	 * @param index the skill index
	 * @return the number of levels gained
	 */
	public int level(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return getLevel(index) - initialLevels[index];
	}

	/**
	 * Calculates the number of levels gained for the given skill index at the given rate.
	 *
	 * @param rate the rate in which to calculate the number of levels gained
	 * @param index the skill index
	 * @return the number of levels gained at the given rate
	 */
	public int level(final Rate rate, final int index) {
		return (int) (level(index) * rate.time / timer.getElapsed());
	}

	/**
	 * Calculates the time to level up at the given rate for the given skill index.
	 *
	 * @param rate the rate in which to calculate the time to level up
	 * @param index the skill index
	 * @return the estimated time to level up at the given rate
	 */
	public long timeToLevel(final Rate rate, final int index) {
		final double exp = experience(rate, index);
		if (exp == 0d) {
			return 0l;
		}
		return (long) (skills.getExperienceToLevel(index, getLevel(index) + 1) / exp * rate.time);
	}
}