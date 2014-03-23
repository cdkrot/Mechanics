package com.cdkrot.mechanics.util;

public class DirectionalVecs {
    public static final VecI3Base Yneg = new VecI3Base(0, -1, 0);
    public static final VecI3Base Ypos = new VecI3Base(0, +1, 0);
    public static final VecI3Base Zneg = new VecI3Base(0, 0, -1);
    public static final VecI3Base Zpos = new VecI3Base(0, 0, +1);
    public static final VecI3Base Xneg = new VecI3Base(-1, 0, 0);
    public static final VecI3Base Xpos = new VecI3Base(+1, 0, 0);
    public final static VecI3Base[] list = new VecI3Base[] { Yneg, Ypos, Zneg, Zpos, Xneg, Xpos };

    private DirectionalVecs() {
    }

    public static boolean isFacingNegative(VecI3Base base) {
        return (base.x + base.y + base.z) < 0;
    }

}
