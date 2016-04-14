package game.states.fight.animation;

/**
 * Types of interpolation for animation steps
 * 
 * @author Fritz
 *
 */
public enum Interpolation {
	
	LINEAR {
		@Override
		public float getInterpolatedValue(float begin, float end, float completion) {
			return (1 - completion) * begin + completion * end;
		}
	}, 
	COS {
		@Override
		public float getInterpolatedValue(float begin, float end, float completion) {
			double c = (1 - Math.cos(completion * Math.PI)) / 2d;
			return  (float) (begin * (1 - c) + end * c);
		}
	},
	SMOOTH_OUT {
		@Override
		public float getInterpolatedValue(float begin, float end, float completion) {
			double c = (1 - Math.cos(completion * Math.PI / 2d));
			return  (float) (begin * (1 - c) + end * c);
		}
	},
	SMOOTH_IN {
		@Override
		public float getInterpolatedValue(float begin, float end, float completion) {
			double c = Math.sin(completion * Math.PI / 2d);
			return  (float) (begin * (1 - c) + end * c);
		}
	},
	NONE {
		@Override
		public float getInterpolatedValue(float begin, float end, float completion) {
			return begin;
		}
	};
	
	/**
	 * Gets an interpolated value
	 * 
	 * @param begin - Initial value
	 * @param end - Target value
	 * @param completion - Percent complete
	 * @return - Interpolated value
	 */
	public abstract float getInterpolatedValue(float begin, float end, float completion);
	
}