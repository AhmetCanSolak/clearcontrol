package rtlib.microscope.lightsheet.calibrator.utils;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;

public class GeometryUtils
{
	public static Line computeYLineOnImage(long lWidth,
											final Vector2D[] lPoints)
	{
		TheilSenEstimator lTheilSenEstimator = new TheilSenEstimator();

		for (int j = 0; j < lPoints.length; j++)
			lTheilSenEstimator.enter(	lPoints[j].getX(),
										lPoints[j].getY());

		UnivariateAffineFunction lModel = lTheilSenEstimator.getModel();
		System.out.println("lModel=" + lModel);

		Vector2D lPointA = new Vector2D(0, lModel.value(0));
		Vector2D lPointB = new Vector2D(lWidth, lModel.value(lWidth));

		System.out.println("lPointA=" + lPointA);
		System.out.println("lPointB=" + lPointB);

		Line lLine = new Line(lPointA, lPointB, 1e-6);
		return lLine;
	}
}
