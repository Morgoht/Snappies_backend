package org.example.models;

import lombok.Data;

@Data
public class Lange extends Article{
    public enum Size {
        S, M, XL
    }

    private Size size;


    public void setSize(String size){
        switch (size) {
            case "S" -> this.size = Size.S;
            case "M" -> this.size = Size.M;
            case "XL" -> this.size = Size.XL;
        }
    }
}
