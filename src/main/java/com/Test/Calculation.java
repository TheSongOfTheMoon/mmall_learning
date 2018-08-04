package com.Test;

import java.util.ArrayList;

public class Calculation {

    public static double CalculationMax(ArrayList sampleList)
    {
        try
        {
            double maxDevation = 0.0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                double max = Double.parseDouble(sampleList.get(0).toString());
                for (int i = 0; i < totalCount; i++)
                {
                    double temp = Double.parseDouble(sampleList.get(i).toString());
                    if (temp > max)
                    {
                        max = temp;
                    }
                } maxDevation = max;
            }
            return maxDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }

    //获取最小值

    public static double CalculationMin(ArrayList sampleList)
    {
        try
        {
            double mixDevation = 0.0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                double min = Double.parseDouble(sampleList.get(0).toString());
                for (int i = 0; i < totalCount; i++)
                {
                    double temp = Double.parseDouble(sampleList.get(i).toString());
                    if (min > temp)
                    {
                        min = temp;
                    }
                } mixDevation = min;
            }
            return mixDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }


}
