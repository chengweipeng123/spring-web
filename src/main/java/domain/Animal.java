package domain;

/**
 * Created by chengseas on 2016/12/6.
 */
public class Animal {
    private String name;
    private Integer price;

    public Animal(String name, Integer price){
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
