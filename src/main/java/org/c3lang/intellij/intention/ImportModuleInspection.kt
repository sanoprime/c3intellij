package org.c3lang.intellij.intention

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.removeUserData
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.completion.ImportCompletionContributor
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.*


class ImportModuleInspection : LocalInspectionTool() {
    private val log = Logger.getInstance(
        ImportCompletionContributor::class.java.name
    )

    override fun getDisplayName(): String = "Import module"

    override fun getGroupDisplayName(): String = "Import"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : C3Visitor() {

            override fun visitCallExpr(psi: C3CallExpr) {
                when (val expr = psi.expr) {
                    is C3PathIdentExpr -> expr.pathIdent.path?.let { registerPathProblem(it) }
                }
            }

            override fun visitBaseType(o: C3BaseType) {
                o.path?.let { registerPathProblem(it) }
            }

            private fun registerPathProblem(psi: C3Path) {
                val callables = NameIndexService.findByNameEndsWith(psi.parent.text, psi.project)
                val element = callables.singleOrNull()

                if (element == null) {
                    val details = callables.joinToString(", ") { it.fqName.fullName }
                    log.debug("Ignoring QuickFix, callables found: {}", details)
                    return
                }
                val importProvider = checkNotNull(psi.parentOfType<C3ImportProvider>())

                val importIntention = element.fqName.module ?: return
                val isShort = psi.text.startsWith(importIntention.suffix)

                if (importProvider.imports.contains(importIntention) && isShort) {
                    return
                }

                val applied = psi.removeUserData(AddImportQuickFix.KEY)
                if (applied == null) {

                    holder.registerProblem(
                        psi,
                        "Import ${importIntention.value}",
                        ProblemHighlightType.WEAK_WARNING,
                        AddImportQuickFix(
                            target = psi,
                            importIntention = importIntention,
                        )
                    )
                }
            }
        }
    }

}