package util

object Html {
  
  def escapeQuotes(s: String): String = s.replace("'", "&rsquo;")

}