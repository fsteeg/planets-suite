<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:t="http://myfaces.apache.org/tomahawk">
      
<body>

  <ui:composition template="inc/header.xhtml">
  
  <ui:define name="header">  	
  	<!-- header navigation-->
    <ui:include src="inc/menu.xhtml">
        <ui:param name="page" value="home"/>
    </ui:include>
    
    <!-- navibar -->
    <!--ui:include src="inc/navibar.load.wf.xhtml"/-->    
  </ui:define>
  
  <ui:define name="title">
        PLANETS: Workflow Online Design Tool (home)
  </ui:define>
  
  <ui:define name="content">
  	<div class="content"> 	

		<h1>IF/5 Online Workflow Design Tool</h1>
		... here be something else
		</div>
  </ui:define>
  
  
  <ui:define name="footer">
  	<ui:include src="inc/footer.xhtml"/>
  </ui:define>

</ui:composition>

</body>
</html>

