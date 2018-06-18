package com.example.zhouge.opencv;

import org.opencv.core.Mat;

import java.io.Serializable;

public class MatSerializable extends Mat implements Serializable {
    public MatSerializable()
    {
        super();
    }

    public MatSerializable(long addr)
    {
        super(addr);
    }



}
