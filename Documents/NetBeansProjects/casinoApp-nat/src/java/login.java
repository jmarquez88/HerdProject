
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class login extends HttpServlet
{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        //url to go to game after login
        String url = "/game.jsp";
        
        //get the action from the form
        String action = (String) request.getParameter("action");
        
        //action new for new user
        //action old for old user login
        if(action.equals("new")) {
        
            //get the inputed values from the form 
            String name = (String) request.getParameter("name");
            String dob = (String) request.getParameter("dob");
            String username = (String) request.getParameter("username");
            String password = (String) request.getParameter("password");
            
            
            //create user object from inputted values
            User user = new User(name , username , password);
            request.setAttribute("user", user);
            
        } else if (action.equals("old")) {
            
            //get the iputted values from the login form
            String username = (String) request.getParameter("username");
            String password = (String) request.getParameter("password");
            
            //check if user exists
            if(username.equals("test")) {
                
                
                //if user exists
                //check if password is correct
                if(password.equals("sesame")) {
                    
                    User user = new User(username , username , password);
                    request.setAttribute("user", user);

                } else {

                    request.setAttribute("error" , "Password Wrong!");
                    url = "/login.jsp";
                }
            
            } else {
            
                request.setAttribute("error", "User not found!");
                url = "/login.jsp";
            }
    
        } else if(action.equals("admin")) {
        
            //get the iputted values from the login form
            String username = (String) request.getParameter("username");
            String password = (String) request.getParameter("password");
            
            if(username.equals("admin")) {
            
                //if admin exists
                //check if password is correct
                if(password.equals("sesame")) {
                    
                    User user = new User(username , username , password);
                    request.setAttribute("user", user);
                    
                    url = "/console.jsp";

                } else {

                    request.setAttribute("error" , "Password Wrong!");
                    url = "/admin.jsp";
                }
                
            } else {
            
                url = "/admin.jsp";
                request.setAttribute("error" , "Admin does not exists!");
                
            }
            
        }
        
        
        
        
        // forward request and response objects to specified URL
        getServletContext()
            .getRequestDispatcher(url)
            .forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
