import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule

def instance = Jenkins.getInstance()

// Disable CLI over remoting
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enable agent-to-master security
instance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

instance.save()
println "Security hardening applied."
