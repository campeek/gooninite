package net.cpeek.gooninite.menus;


public record HoverRegion(int x1, int y1, int x2, int y2) {
    public boolean isInside(int x, int y){
        if(x >= x1 && x <= x2){
            if(y >= y1 && y <= y2){
                return true;
            }
        }
        return false;
    }
}
