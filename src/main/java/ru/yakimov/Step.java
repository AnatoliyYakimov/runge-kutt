package ru.yakimov;

import java.util.ArrayList;
import java.util.List;

public class Step {

	private double hMin;
	private double hMax;
	private double eps;

	private int iterationsWithMinStep = 0;
	private int iterationsWithMaxStep = 0;
	private int iterationsWithErrorMoreThanEpsilon = 0;

	private List<Double> steps = new ArrayList<>();

	public Step(double hMin, double hMax, double eps) {
		this.hMin = hMin;
		this.hMax = hMax;
		this.eps = eps;
	}

	public double estimate(double hn, double localError) {
		if (localError > eps) {
			iterationsWithErrorMoreThanEpsilon++;
			if (hn < hMin) {
				iterationsWithMinStep++;
				hn = hMin;
			}
			else if (hn > hMax) {
				iterationsWithMaxStep++;
				hn = hMax;
			}
			else {
				hn = 0.9 * Math.sqrt(Math.sqrt(eps / Math.abs(localError))) * hn;
			}
		}
		steps.add(hn);
		return hn;
	}

	public double estimateStartingStep(double A, double B) {
		double hn = (B - A) / 10;
		if (hn < hMin) {
			iterationsWithMinStep++;
			hn = hMin;
		}
		else if (hn > hMax) {
			iterationsWithMaxStep++;
			hn = hMax;
		}
		steps.add(hn);
		return hn;
	}

	public boolean isAccuracyAchieved() {
		return iterationsWithErrorMoreThanEpsilon == 0;
	}

	public double gethMin() {
		return hMin;
	}

	public double gethMax() {
		return hMax;
	}

	public double getEps() {
		return eps;
	}

	public int getIterationsWithMinStep() {
		return iterationsWithMinStep;
	}

	public int getIterationsWithMaxStep() {
		return iterationsWithMaxStep;
	}

	public int getIterationsWithErrorMoreThanEpsilon() {
		return iterationsWithErrorMoreThanEpsilon;
	}

	public List<Double> getSteps() {
		return steps;
	}
}
