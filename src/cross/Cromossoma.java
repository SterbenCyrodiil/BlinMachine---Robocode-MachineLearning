package cross;

import impl.Point;
import interf.IPoint;
import interf.IUIConfiguration;
import maps.Maps;
import viewer.PathViewer;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cromossoma implements Comparable<Cromossoma> {

    public static IUIConfiguration conf;

    static {
        try {
            conf = Maps.getMap(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int maxTam = 5;
    private int minTam = 0;
    private int maxMap = 600;
    private int minMap = 0;

    private int colisionWeight = 10000;

    private double totaldist;

    protected int tam = numeroAleatorio(minTam,maxTam);
    public List<IPoint> points = new ArrayList<>();
    public List<Rectangle> rectangles;

    public Cromossoma() {
        points.add(conf.getStart());
        this.starting();
        points.add(conf.getEnd());
        rectangles = conf.getObstacles();
        totaldist = 0.0;
    }



    public void starting(){
        for(int i =0;i<tam;i++){
            int x = numeroAleatorio(minMap,maxMap);
            int y = numeroAleatorio(minMap,maxMap);
            points.add(new Point(x,y));

        }
    }

    public int numeroAleatorio(int min, int max){

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void map(){
        PathViewer pv = new PathViewer(conf);
        pv.setFitness(9999);
        pv.setStringPath("test");
        pv.paintPath(points);
    }

    public void getPoints(){
        for(int ix=1; ix<this.points.size(); ix++){
            Point point = (Point) this.points.get(ix);
            Point prevPoint = (Point) this.points.get(ix-1);
            System.out.println(prevPoint.getX()+"; "+ point.getX()+"; "+prevPoint.getY()+"; "+ point.getY());
        }
    }

    public boolean colisionChecker(){

        boolean colflag = false;

        for(int ix=1; ix<this.points.size(); ix++){
            Point point = (Point) this.points.get(ix);
            Point prevPoint = (Point) this.points.get(ix-1);
            Line2D line2d = new Line2D.Double(prevPoint.getX(), point.getX(), prevPoint.getY(), point.getY());
            // System.out.println(prevPoint.getX()+"; "+ point.getX()+"; "+prevPoint.getY()+"; "+ point.getY());
            for(int jx=0; jx<this.rectangles.size(); jx++){
                boolean collided = line2d.intersects(this.rectangles.get(jx));
                if(collided){
                    colflag = true;
                }
                //System.out.println("Rectangle: "+jx+"; Touches: "+collided);
            }
        }
        return colflag;
    }

    public double getDistance(){
        totaldist = 0.0;
        for(int ix=1; ix<this.points.size(); ix++){
            Point point = (Point) this.points.get(ix);
            Point prevPoint = (Point) this.points.get(ix-1);
            totaldist = totaldist + Math.sqrt((point.getY() - prevPoint.getY()) * (point.getY() - prevPoint.getY()) + (point.getX() - prevPoint.getX()) * (point.getX() - prevPoint.getX()));
        }
        return totaldist;
    }

    public Cromossoma[] cross1(Cromossoma other){

        Cromossoma filho1 = new Cromossoma();
        Cromossoma filho2 = new Cromossoma();

        filho1.points.clear();
        filho2.points.clear();

        filho1.points.add(conf.getStart());
        filho2.points.add(conf.getStart());

        //logica
        int sizePai = this.points.size();
        int sizeMae = other.points.size();


        for (int i = 1; i <sizePai-1 ; i++) {
            int x = this.points.get(i).getX()+other.points.get(i).getX();
            int y = this.points.get(i).getY()+other.points.get(i).getY();
            filho1.points.add(new Point(x/2,y/2));
            filho2.points.add(new Point(x/2,y/2));

        }

        filho1.points.add(conf.getEnd());
        filho2.points.add(conf.getEnd());

        Cromossoma[] novos = {filho1, filho2};

        return novos;
    }

    public Cromossoma[] cross2(Cromossoma other){

        Cromossoma filho1 = new Cromossoma();
        Cromossoma filho2 = new Cromossoma();

        filho1.points.clear();
        filho2.points.clear();

        filho1.points.add(conf.getStart());
        filho2.points.add(conf.getStart());


        int sizePai = this.points.size();
        int sizeMae = other.points.size();

        //logica filho1
        for (int i = 1; i <sizePai-1 ; i++) {
            int x = this.points.get(i).getX()+other.points.get(i).getX();
            int y = this.points.get(i).getY()+other.points.get(i).getY();
            filho1.points.add(new Point(x/2,y/2));
        }

        //logica filho 2
        for (int i = 1; i <sizeMae-1 ; i++) {
            int x = this.points.get(i).getX()+other.points.get(i).getX();
            int y = this.points.get(i).getY()+other.points.get(i).getY();
            filho2.points.add(new Point(y/2,x/2));
        }

        filho1.points.add(conf.getEnd());
        filho2.points.add(conf.getEnd());


        Cromossoma[] novos = {filho1, filho2};

        return novos;
    }

    public double getFitness(){
        double value = 0.0;

        if(colisionChecker()){
            value = colisionWeight;
        }

        value = value + getDistance();

        return value;
    }

    @Override
    public int compareTo(Cromossoma o) {
        if (o.getFitness() < this.getFitness())
            return 1;
        else if (o.getFitness() > this.getFitness())
            return -1;
        else return 0;
    }

    @Override
    public String toString() {
        return "Fitness: "+ getFitness()
                +"Pontos: ";
    }
}
