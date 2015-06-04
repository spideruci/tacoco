package org.spideruci.tacoco.coverage;

public class SourceFile {
  final String fullName;
  final int firstLine;
  final int lastLine;
  
  public SourceFile(String fullName, int firstLine, int lastLine) {
    this.fullName = fullName;
    this.firstLine = firstLine;
    this.lastLine = lastLine;
  }
  
  public int getLineCount() {
    return lastLine;
  }
  
  public int getRealLineCount() {
    return lastLine - firstLine + 1; 
  }
  
  public String getFullName() {
    return this.fullName;
  }

  @Override
  public int hashCode() {
    return this.fullName.hashCode();
  }
  
  @Override
  public boolean equals(Object object) {
    if(object == null) {
      return false;
    }
    
    if(!(object instanceof SourceFile)) {
      return false;
    }
    
    SourceFile sourceFile = (SourceFile) object;
    
    if(this.fullName.equals(sourceFile.fullName)) {
      return true;
    }
    
    return false;
  }
  
  

  /**
   * @return the firstLine
   */
  public int getFirstLine() {
    return firstLine;
  }

  /**
   * @return the lastLine
   */
  public int getLastLine() {
    return lastLine;
  }
}