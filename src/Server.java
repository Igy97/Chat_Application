import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

class Nit extends Thread
{
    Socket s;
    PrintStream out;
    Scanner in;
    ArrayList<Nit> niti;
    String ime;
    String poruka;
    String korisnici = "";
    //int broj_niti;


    Nit(Socket s, ArrayList<Nit> niti) throws IOException
    {
        this.niti = niti;
        this.s = s;
        out = new PrintStream(s.getOutputStream());
        in = new Scanner(s.getInputStream());
        ime = in.nextLine();
    }

    @Override
    public void run()
    {
        System.out.println("Povezao se:" + ime);
        for(Nit t: niti) korisnici+=t.ime + "_";  //sklapa sve aktive korisnike u jedan string

        //start
        for(Nit t: niti)
        {
            //salje poruku svima da je korisnik online
            t.out.println("Poruka");
            t.out.println("Korisnik:" + ime + " se prikljucio chat-u!");
            //zatim salje poruku da smesti sve korisnike koji su online
            t.out.println("Korisnici");
            t.out.println(korisnici);
        }
        try
        {
            while(true)
            {
                poruka = in.nextLine();  //ceka poruku
                if(!poruka.equals("Gasi"))
                {
                    for(Nit t: niti)
                    {
                        if(!t.equals(this))
                        {
                            System.out.println(ime + poruka);
                            t.out.println("Poruka");
                            t.out.println(ime + ":" + poruka);
                        }
                    }
                }
                else
                {
                    out.println("Gasi");
                    break;
                }
            }
        }
        catch (Exception e){e.printStackTrace();}

        synchronized (niti)
        {
            niti.remove(niti.indexOf(this));  //brise trenutnog korisnika
            korisnici = "";
            for(Nit t: niti)
            {
                korisnici+=t.ime + "_";
                t.out.println("Poruka");
                t.out.println("Korisnik:" + ime + " je napustio chat!");
            }
            System.out.println(korisnici);
            for(Nit t: niti)
            {
                t.out.println("Korisnici");
                t.out.println(korisnici);
            }
        }
    }
}


public class Server
{

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(8000);
        ArrayList<Nit> niti = new ArrayList<>();
        System.out.println("Server je startovan!");
        while(true)
        {
            niti.add(new Nit(ss.accept(), niti));
            niti.get(niti.size()-1).start();
        }
    }

}
