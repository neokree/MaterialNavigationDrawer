package it.neokree.materialnavigationdrawer.elements;

/**
 * Created by neokree on 04/03/15.
 */
public class Element {

    public static final int TYPE_SECTION = 0;
    public static final int TYPE_DIVISOR = 1;
    public static final int TYPE_SUBHEADER = 2;
    public static final int TYPE_BOTTOM_SECTION = 3;

    private int type;
    private Object element;

    public Element(int type,Object element) {
        this.type = type;
        this.element = element;
    }

    // setters

    public void setType(int type) {
        this.type = type;
    }

    public void setElement(Object element) {
        this.element = element;
    }

    // getters

    public int getType() {
        return type;
    }

    public Object getElement() {
        return element;
    }

}
