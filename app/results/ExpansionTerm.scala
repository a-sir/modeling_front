package results

/**
 * @author A.Sirenko
 *          Date: 5/25/14
 */
case class ExpansionTerm(id: Int, name: String)

object ExpansionTerm {

  val mockTerms: List[ExpansionTerm] = List(
    ExpansionTerm(1, "apple"),
    ExpansionTerm(2, "peach"),
    ExpansionTerm(3, "woody allen")
  )

}
